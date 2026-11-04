#!/usr/bin/env python3
"""tour_shots.py <outdir> lang [lang...] : Free Mode, then the tour (step 1 and step 7) -> raw frames + texts."""
import os, re, subprocess, sys, time, xml.etree.ElementTree as ET
D=__import__('os').environ.get('PHONE','1d04811b'); PKG='com.abacus.puzzle.l10n'; ACT=PKG+'/com.jigar.me.ui.view.home.HomeActivity'
def adb(*a, raw=False, t=60):
    r=subprocess.run(['adb','-s',D,*a],capture_output=True,timeout=t); return r.stdout if raw else r.stdout.decode('utf-8','ignore')
def nodes():
    adb('shell','uiautomator','dump','/sdcard/t.xml')
    try: root=ET.fromstring(adb('exec-out','cat','/sdcard/t.xml'))
    except Exception: return []
    return [((n.get('text') or n.get('content-desc') or '').strip(), n.get('bounds')) for n in root.iter('node') if (n.get('text') or n.get('content-desc') or '').strip()]
def tap(b):
    x1,y1,x2,y2=map(int,re.findall(r'\d+',b)); adb('shell','input','tap',str((x1+x2)//2),str((y1+y2)//2))
out=sys.argv[1]; os.makedirs(out,exist_ok=True)
for lang in sys.argv[2:]:
    adb('shell','cmd','locale','set-app-locales',PKG,'--locales',lang)
    adb('shell','input','keyevent','KEYCODE_WAKEUP'); adb('shell','wm','dismiss-keyguard')
    adb('shell','am','force-stop',PKG); adb('shell','am','start','-n',ACT,'--es','deep_link_route','AbacusFreeMode'); time.sleep(8)
    n=nodes(); open(f'{out}/free_{lang}.raw','wb').write(adb('exec-out','screencap',raw=True))
    print(lang,'FREE |',' | '.join(t for t,_ in n)[:420])
    # the tour button is the 1st text whose neighbours are the tour label: find by position (top right pill)
    cand=[b for t,b in n if t and ('tour' in t.lower() or 'visite' in t.lower() or 'recorrido' in t.lower() or 'tur' in t.lower() or 'ツアー' in t or 'ทัวร์' in t or 'тур' in t.lower())]
    if cand:
        tap(cand[0]); time.sleep(3)
        n=nodes(); open(f'{out}/tour1_{lang}.raw','wb').write(adb('exec-out','screencap',raw=True))
        print(lang,'TOUR1 |',' | '.join(t for t,_ in n)[:300])
        for _ in range(6):
            nx=[b for t,b in n if '→' in t or 'Next' in t or '次へ' in t or 'ถัดไป' in t]
            if not nx: break
            tap(nx[0]); time.sleep(1.5); n=nodes()
        open(f'{out}/tour7_{lang}.raw','wb').write(adb('exec-out','screencap',raw=True))
        print(lang,'TOUR7 |',' | '.join(t for t,_ in n)[:300])
    else: print(lang,'no tour button found')
adb('shell','cmd','locale','set-app-locales',PKG,'--locales','')

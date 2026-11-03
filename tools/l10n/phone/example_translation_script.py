import json, glob
N=' '
L7=['fr','es','pt','tr','ru','ja','th']
T={
'ccm_listen_hint':['Écoute bien chaque nombre','Escucha con atención cada número','Ouça com atenção cada número','Her sayıyı dikkatle dinle','Внимательно слушай каждое число','ひとつひとつの数をよく聞いてね','ฟังตัวเลขแต่ละตัวให้ดี'],
'ccm_number':['Nombre','Número','Número','Sayı','Число','数','ตัวเลข'],
'ccm_speak':['Voix','Voz','Voz','Ses','Голос','音声','เสียง'],
'ccm_word':['Mots','Palabras','Palavras','Kelime','Слова','言葉','คำ'],
'check_answer':['Vérifier la réponse','Comprobar respuesta','Verificar resposta','Cevabı kontrol et','Проверить ответ','答え合わせ','ตรวจคำตอบ'],
'congratulations':['Félicitations…','Felicidades…','Parabéns…','Tebrikler…','Поздравляем…','おめでとう…','ขอแสดงความยินดี…'],
'create_custom_challenge_as_per_your_kid_s_ability':["Créez un défi personnalisé\nselon les capacités de votre enfant",'Crea un reto personalizado\nsegún la capacidad de tu hijo','Crie um desafio personalizado\nde acordo com a habilidade da criança','Çocuğun yeteneğine göre\nözel bir meydan okuma oluşturun','Создайте свой вызов\nпод способности ребёнка','お子さまの力に合わせて\nオリジナルチャレンジを作りましょう','สร้างชาเลนจ์ที่กำหนดเอง\nให้เหมาะกับความสามารถของเด็ก'],
'gap_between_two_question_sec':['Pause entre deux questions (en s)','Pausa entre dos preguntas (en s)','Intervalo entre duas perguntas (em s)','İki soru arası bekleme (sn)','Пауза между вопросами (в сек.)','問題と問題の間隔（秒）','ช่วงเวลาระหว่างสองข้อ (วินาที)'],
'no_i_want_to_close':['Non, je veux fermer','No, quiero cerrar','Não, quero fechar','Hayır, kapatmak istiyorum','Нет, хочу закрыть','いいえ、閉じます','ไม่ ฉันต้องการปิด'],
'number_of_questions':['Nombre de questions','Número de preguntas','Número de perguntas','Soru sayısı','Количество вопросов','問題の数','จำนวนข้อ'],
'question_display_options':["Options d'affichage",'Opciones de visualización','Opções de exibição','Görüntüleme seçenekleri','Параметры показа','表示のオプション','ตัวเลือกการแสดงผล'],
'sorry':[f'Désolé{N}!','¡Lo siento!','Que pena!','Üzgünüm!','Увы!','ざんねん！','เสียใจด้วย!'],
'start_new_challenge':['Commencer un nouveau défi','Empezar un nuevo reto','Começar um novo desafio','Yeni meydan okumayı başlat','Начать новый вызов','あたらしいチャレンジをはじめる','เริ่มชาเลนจ์ใหม่'],
}
done=set()
for f in glob.glob('docs/l10n/translations/*.json'):
    if f.endswith('m11_ccm.json'): continue
    done|=set(json.load(open(f,encoding='utf-8')))
out={k:dict(zip(L7,v)) for k,v in T.items() if k not in done}
print('already done:',[k for k in T if k in done])
json.dump(out,open('docs/l10n/translations/m11_ccm.json','w',encoding='utf-8'),ensure_ascii=False,indent=1)
print(len(out))

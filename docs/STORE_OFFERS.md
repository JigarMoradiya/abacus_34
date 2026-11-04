# Store offers: what you set in the store shows in the app

Rule: a store offer and the timed `.offer` product (Remote Config `home_offer`) are **never set together**. While the timed `.offer` plan replaces a
plan, no store offer is read for that plan.

## Subscriptions (Android: Google Play, iOS: App Store)
Whatever the store offers on the plan is read through RevenueCat and shown: a **free trial** ("7 Days Free, then ...") and a **paid intro price**
(the intro price with the regular price struck through, "N% OFF" worked out from the two store prices, and "first year, then ... / year").
Nothing to set in Firebase.

## Lifetime / one-time product, Android (Google Play Console)
Play Console > Monetize > One-time products > `com.abacus.all` > Purchase options and offers > **Add offer > Discount**:
- **Percentage**: the card shows the regular price struck through, the offer price and "**N% OFF**" (the percentage you typed).
- **Absolute discount**: the card shows the regular price struck through, the offer price and "**₹500 OFF**" (the amount you typed, in the user's currency).
- A time window (start and end) works: the offer shows only inside it; when it ends within a day the card also shows a live "Ends in" countdown.
- Country / region eligibility, purchase limits and offer tags are Play's: the app shows the offer only when Play lists it for that person.
- No offer in the console: the lifetime plan looks and is bought exactly as before (through RevenueCat).

**Home page:** while such an offer is on, the same banner as the timed Home offer shows at the bottom of Home ("Special price!" and "30% OFF on the
Lifetime plan" or "₹500 OFF on the Lifetime plan", with an "Ends in" countdown only when it ends within a day). Tapping it opens the plans. It is never
shown together with the timed Home offer (`home_offer`): while that one runs it has the banner. Premium users do not see it.

Why the app buys it itself: RevenueCat's Android SDK (10.21) reads only the normal price of a one-time product and never sets the offer token,
so a purchase through it would charge the FULL price while the card shows the offer. While an offer exists the app therefore buys through Google Play
directly (`StoreOfferPurchaser`, with the offer token), acknowledges the purchase, then calls RevenueCat `syncPurchases` so premium access is granted
and sends the purchase to the app's server as for any other. If the offer ended between the card and the tap nothing is charged: the screen refreshes.

Switch: Remote Config `store_one_time_offers`, on unless it says `off` / `false` / `0` (turns the Play offer display and its purchase path off
without a release; the lifetime plan then sells at the normal price through RevenueCat).

**Test before relying on it** (this part cannot be tested without the store): create a Discount offer, install the build from the Play internal
testing track with a licence-tester account, open the paywall, check the price in the Google Play sheet equals the price on the card, buy, and check
that premium unlocks and that the purchase appears in RevenueCat. Remove the test purchase afterwards (Play Console > Order management).

## Lifetime / one-time product, iOS (App Store Connect)
Apple has no discount for a non-consumable that an app can read: only offer codes (redeemed by typing a code) and price changes. A price change shows
at once as the product's price (no "was" price or % is available from the store). A timed lifetime sale on iOS uses the `.offer` product
(`home_offer`, `"product": "lifetime"`).

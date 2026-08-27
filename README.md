# FinFlow: Projekt pri predmetu TNUV
## Aplikacija za pametno upravljanje osebnih financ

FinFlow je sodobna Android aplikacija, zasnovana za preprosto in pregledno spremljanje osebnih financ. Uporabnikom pomaga razumeti njihove potrošniške navade, določiti mesečne proračunske omejitve in dosegati varčevalne cilje.

Aplikacija temelji na treh pogledih, ki so dostopni preko spodnje navigacijske vrstice: **Poraba**, **Proračun** in **Cilji**.

---

## Funkcionalnosti po prikazih

### 1. Spremljanje porabe
Zavihek **Poraba** je osrednji prostor za beleženje odhodkov. Njegov namen je uporabniku podati vpogled v to, kam gre njegov denar.
*   **Vizualna analiza**: Tortni diagram (Pie Chart) prikazuje razdelitev stroškov po kategorijah (Hrana, Kava, Prevoz, Stanovanje, Tehnologija, Drugo).
*   **Zgodovina transakcij**: Seznam **Nedavni stroški** prikazuje zadnje vnose v obliki preglednih kartic, ki vključujejo naziv, kategorijo in znesek.
*   **Hitro dodajanje**: Enostaven obrazec za vnos novih stroškov z izbiro kategorije in opcijskim opisom.
*   **Upravljanje**: Možnost urejanja ali brisanja obstoječih stroškov s klikom na meni ob posameznem vnosu.

### 2. Upravljanje proračuna
Zavihek **Proračun** je namenjen načrtovanju porabe. Namen je preprečiti porabo preko nastavljega mesečnega limita.
*   **Mesečni limit**: Uporabnik si lahko nastavi poljuben mesečni proračun.
*   **Indikator napredka**: Progress bar vizualno prikazuje, kolikšen delež proračuna je že porabljen in koliko sredstev še ostane do konca meseca.
*   **Pametni nasveti**: Aplikacija določa kategorije z najvišjo porabo in uporabniku ponudi nasvete za varčevanje, ki izhajajo iz njegove dosedanje porabe.

### 3. Varčevalni cilji
Zavihek **Cilji** motivira uporabnika k varčevanju za specifične nakupe ali dogodke (npr. nov telefon, potovanje, avto...).
*   **Spremljanje napredka**: Vsak cilj ima svojo kartico s Progress bar-om in izračunom preostalega zneska do uresničitve.
*   **Skupni pregled**: Zgornji del zavihka prikazuje skupen privarčevan znesek vseh ciljev skupaj, dopolnjen z vizualizacijo s tortnim diagramom.
*   **Interaktivnost**: Uporabnik lahko neposredno na kartici cilja dodaja ali odšteva privarčevana sredstva.
*   **Kategorizacija**: Vsak cilj je opremljen s kategorijo.

---

### Tehnične podrobnosti (pristopi, ki jih nismo obravnavali na vajah)
* **Lokalna shramba**: Podatki o porabi so shranjeni v lokalni bazi **Room**.
* **MPAndroidChart**: Knjižnica za grafično upodabljanje, uporabljena za prikaz porabe s tortnimi diagrami.
* **Fragmenti**: Aplikacija implementira tri glavne fragmente za učinkovitejše preklapljanje med aktivnostmi in izboljšano modularnost.

Nejc Simčič (ns16275@student.uni-lj.si) in Nik Stefančič (ns88618@student.uni-lj.si), Avgust 2026, FE UL

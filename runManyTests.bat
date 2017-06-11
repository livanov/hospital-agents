SET levels=crunch firefly Heuristics1 MoveSatGoal SAD1 SAD2 MA1 MA2 MultBoxMa2 MA2colors
SET envlevels=SAbispebjerg SABoxesOfBitch SAboxesOfHanoi SAchoice SAchoice2 SAsimple1 SAsimple2 SAsokobanLevel96 SAtest MASimple1 
:SAAnagram SApushing

DEL results.txt

FOR %%A IN (%levels%) DO (

echo %%A >> results.txt
java -cp classes Main -file=levels\%%A.lvl >> results.txt
echo ============================================== >> results.txt
)

FOR %%A IN (%envlevels%) DO (

echo environment\%%A >> results.txt
java -cp classes Main -file=levels\environment\%%A.lvl >> results.txt
echo ============================================== >> results.txt
)

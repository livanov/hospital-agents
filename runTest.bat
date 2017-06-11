:works
REM SET levelname=Firefly.lvl
:works
REM SET levelname=SAmoveSatGoal.lvl
:works
REM SET levelname=MAHeuristics.lvl
:noops while waiting other agent to satisfy the goal he is standing on
REM SET levelname=MAtakeTurns.lvl
SET levelname=sajasonfour.lvl
REM SET levelname=majasonfour.lvl
:works
REM SET levelname=mareplan.lvl
:works
REM SET levelname=SAboxesafteragoal.lvl
:works
REM SET levelname=environment\masimple1.lvl
:works
REM SET levelname=environment\SAanagram.lvl
:works
REM SET levelname=environment\SAsokobanLevel96.lvl
:runs out of memory
REM SET levelname=environment\SAbispebjerg.lvl
:works
REM SET levelname=environment\saboxesofhanoi.lvl
:works
REM SET levelname=environment\saboxesofbitch.lvl
:gets in a conflict, starts printing NoOps only
REM SET levelname=environment\mamultiagentsort.lvl
:some of the agents cant find a way to his goal, thus exploring the whole state space - runs out of memory quite fast.
REM SET levelname=environment\machallenge.lvl
:works
REM SET levelname=environment\mapacman.lvl
java -jar server.jar -l levels\%levelname% -g 50 -c "java -cp classes Main"
pause
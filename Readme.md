# U06 | Timer

## Aufgabe

In dieser Aufgabe implementieren Sie einen Timer. Die Anwendung zählt die Zeit herunter und informiert den User, sobald die Zeit abgelaufen ist. Durch Verwendung eines parallelen Threads und eines Service sorgen wir dafür, dass der UI Thread nicht blockiert wird und der Timer auch weiterläuft, wenn die Anwendung im Hintergrund oder geschlossen ist

Das User Interface und einige Hilfsklassen haben wir für Sie vorbereitet. Im Starterpaket finden Sie die `TimerActivity`. Über 3 `EditText` ist hier eine Eingabe für Stunden, Minuten und Sekunden möglich. Die verbleibende Zeit wird ebenfalls hier angezeigt.

## Wichtige Stellen im vorgegebenen Code

In der `TimerActivity` finden Sie bereits 2 hilfreiche Methoden, `initUI()`, welche dafür sorgt, dass die View Elemente bereits aus dem Layout ausgelesen werden und `getSetTime()` welche Ihnen ein int-array liefert, das die 3 Inputs der EditText-Felder beinhaltet, ist eines der Felder leer wird an dieser Stelle 0 zurückgeliefert.

In der `Timer`-Klasse finden Sie die `run`-Methode welche aus dem _Runnable_ Interface überschrieben wird, hier wird später die eigentliche Timerlogik sein, außerdem finden Sie hier die Methode `getFormattedStringFromInt(int seconds)`, welche einen Integer Wert als Parameter übergeben bekommt und Ihnen einen String liefert, der diesen Wert in dem Format ##:##:## darstellt. 

## Vorgehen

### Schritt 1 - Vorbereitung

Laden Sie da Starterpaket herunter und verschaffen Sie sich einen Überblick über den vorgegebenen Code. Starten Sie die App im Emulator um sicherzustellen, dass keine Fehler auftreten.

**Zwischenziel 1: Das Starterpaket lässt sich problemlos öffnen**

### Schritt 2 - Auslesen von Eingaben

Implementieren Sie eine Funktion welche überprüft, ob die Eingaben valide sind (die Eingaben sind zum Beispiel dann invalide, wenn für Minuten oder Sekunden ein größerer Wert als 59 gewählt wurde). Sind die Eingaben nicht valide, so soll der/die Nutzer|in darauf hingewiesen werden, andernfalls sollen alle Angaben in Sekunden umgerechnet und zu einem Integer Wert zusammengefasst werden.

**Zwischenziel 2: Die App erkennt falsche Inputs, richtige Inputs werden als Gesamtsekunden angezeigt**

### Schritt 3 - Implementieren des Timers

Passen Sie die Timer Klasse so an, dass diese einen Integer Wert als Variable hat. Anschließend können Sie mit Hilfe der Executors Klasse die `run()`-Funktion eines Runnables wiederholt und in einem bestimmten Intervall ausführen:
```
ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
ScheduledFuture sf = executor.scheduleAtFixedRate(Runnable runnable, Initial_delay, delay, TimeUnit);

```
Die Methode `scheduleAtFixedRate` liefert ein Objet der Klasse `ScheduledFuture`, um die wiederholte Ausführung des Runnables abzubrechen könne Sie auf dem ScheduledFuture-Objekt die Methode `cancel()` aufrufen.
Legen Sie nun ein Objekt der Klasse Timer in der TimerActivity an und registrieren Sie diese als den listener, dann können Sie mit Hilfe der `onTimerUpdate(int remainingTime)` die TextView immer aktualisieren, wenn ein Tick erfolgt.

**Zwischenziel 3: Der Timer funktioniert wenn er in der Activity angelegt wird und die Anzeige wird jeden Tick aktualisiert**

### Schritt 4 - Auslagern in einen Service

Erstellen Sie nun einen neuen Vordergrund-Service, dieser wird sich darum kümmern, die Funktionalität der App im Hintergrund zu regeln, sodass unser Timer auch funktioniert, wenn die App nicht aktiv ist. Sorgen Sie durch den Methodenaufruf `startForeground()` in der onCreate Methode des Service dafür, dass dieser im Vordergrund läuft. Android verlangt hier, dass der Service eine Notifiation schickt, damit dem/der Nutzer|in klar ist, dass noch Teile der App aktiv sind, auch wenn ggf. keine Activity mehr aktiv ist. Das erstellen und schicken von Notifications wird im [Create a Notification Guide](https://developer.android.com/training/notify-user/build-notification) auf der Android developer page erklärt. Beachten Sie hier vor allem, dass Sie einen Notification Channel ertsellen müssen, über welchen später die Notifications geschickt werden können. 

_Hinweis: Denken Sie daran, logisch unabhängige Teile Ihrer Anwedung zu trennen, zum Beispiel durch das erstellen einer Klasse, welche sich um die Notifications kümmert_


Verlagern sie den Timer in den neu erstellten Service und benutzen Sie [Broadcasts](https://developer.android.com/guide/components/broadcasts#context-registered-receivers) um mit der Activity zu kommunizieren, nutzen Sie hierzu die Methode `sendBroadcast(Intent intent)`. Den zu übergebenden Intent liefern Ihnen Methoden der bereits gegebenen Klasse des `TimerBroadcastReceivers`.

Weitere Informationen zum Foreground-Service finden Sie bei der [Foreground Service Documentation](https://developer.android.com/guide/components/foreground-services) der Android developer page, ebenso eine [Übersicht über Services](https://developer.android.com/guide/components/services).

**Zwischenziel 4: Bei einem Klick auf den Timer starten Button wird nun ein ForegroundService gestartet, in welchem der Timer läuft. Der/Die Nutzer|in wird durch eine Notification darüber informiert**

### Schritt 5 - Anpassen der Notification

Den Inhalt und Titel einer Notification können Sie anpassen, indem Sie eine neue Notification mit gleicher ID anzeigen lassen. Das Android-System kümmert sich darum, dass die bereits bestehende Notification angepasst wird, statt jedes mal eine neue zu schicken.

**Zwischenziel 5: Die Notification des Service wird mit jedem Tick angepasst und zeigt die verbleibende Zeit an**

### SChritt 6 - Einrichten eines WakeLock und persistieren des TimerState

Die App funktioniert nun auch, wenn Sie im Hintergrund läuft, allerdings nicht wenn das Handy im Standby ist. Damit die App auch dann noch Rechenleistung von der CPU bekommt wenn das Handy im Standby ist muss ein WakeLock implementiert werden, alles wichtige dazu finden Sie auf der Android Developer Seite [Keep the device awake](https://developer.android.com/training/scheduling/wakelock#cpu).

Letztlich muss noch sichergestellt werden, dass die Buttons der App richtig eingestellt sind (Der Timer starten Button sollte nie verfügbar sein, wenn bereits ein Timer läuft). Hierzu verwenden Sie die Klasse TimerStateStorage sowie das Enum TimerState, die dort vorgegebenen Methoden kümmern sich darum, beim starten bzw stoppen des Timers den Zustand abzuspeichern. Beim nächsten öffnen der App können Sie die gespeicherten Daten auslesen und die Buttons entsprechend anpassen.

_Hinweis: Um sicherzuegehen, dass diese Überprüfung stattfindet ist es ratsam die Methode `onResume` der Activity zu überschreiben. Diese wird in jedem Fall aufgerufen, wenn der User die Activity öffnet, auch wenn Sie lediglich aus dem Hintergund wieder in den Vordergrund geschoben wird._

**Zwischenziel 6: Die App läuft auch im Standby problemlos weiter, beim erneuten öffnen der Activity werden die Buttons richtig dargestellt**

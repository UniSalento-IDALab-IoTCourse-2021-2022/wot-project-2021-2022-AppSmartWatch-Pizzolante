# SMART GRID - WorkSafe Watch Application

## Introduzione
L'applicazione nasce per essere utilizzata su dispositivi indossabili quali smartwatch
basati su Android. Lo scopo dell'app è quello di fornire ulteriore supporto alla figura
del Machinist nello scenario della SMART GRID. In particolare l'app esegue le stesse funzioni
del ruolo Machinist dell'app Android per Mobile. (Per ulteriori info consultare:
https://github.com/UniSalento-IDALab-IoTCourse-2021-2022/wot-project-2021-2022-AndroidApplication-Pizzolante)

## Utilizzo
Dopo l'installazione dell'app sul dispositivo indossabile, è necessario connettere il
dispositivo alla stessa rete alla quale sono connessi gli altri device (quali smartphone e
server). A questo punto è necessario disabilitare l'accoppiamento tramite Bluetooth 
a qualsiasi dispositivo. In questo modo tutte le richieste HTTP verranno redirette 
seguendo l'indirizzo IP della rete in cui lo smartwatch si trova.

Per ricevere le notifiche di allarme è necessario:
1. Avviare l'app
2. Selezionare "Inizia"
3. Selezionare l'ID del beacon posto sul proprio macchinario.
4. Premere su "Ok"

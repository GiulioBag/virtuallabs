# VISTE ESPOSTE AL CLIENT DIVICE PER CONTROLLER

TUTTE le seguenti viste devono essere validate e discusse da chi le implementa lato client.
TUTTi i metodi in caso si errori lanciano un'eccezione. I messaggi delle eccezioni sono personalizzati e stampabili.
Salvo altra indicazioni per accedere alle uri il cui prefisso è API si deve essere loggati.

### USER CONTROLLER ('/API/users') 
I suoi metodi possono essere eseguiti da chiunque tranne per il signout.

- '/{userId}': (GET) Rende lo userDTO con l'id indicato nell'url.

- '/signup' : (POST) Server per registrare un nuovo utente, non rende niente. Gli si deve passare un JSON del tipo: 
{
	"serialNumber":"s1",
	"lastName":"Bagnoli",
	"name":"Giulio",
	"email":"s1@studenti.polito.it",
    "password":"Kioiki17", 
    "photo": byte []
}

- '/confirm/{tokenId}': (GET) Serve per confermare la email dell'utente. E' accessibile dal link mandato per email. Qualunque altra azione fatta dall'utente prima della conferma del link viene respinta.
 Rende un bool a seconda ce il token passato sia corretto o meno. Accessibile tramite mail di conferma, ignorabile dal client.

- '/signin': (POST) Serve per far accedere l'utente al sito, gli si deve passare un JSON del tipo:
{
	"username":"s1",
	"password":"Kioiki17"
}
Server replica con 200-ok se login va bene, 401-unauthorized se login errato

- '/signout': (GET) Serve per disabilitare il JWToken prima dello scadere del tempo, praticamente è il logout. Contrariamente alle altre viste si deve essere autenticati per effeettuarla -> nell'header deve essere presente un campo 'Authorization'.

### TEAM CONTROLLER ('API/teams')

- '/{teamId}/members' : (GET) Rende una lista di StudentDTO che appartengono al team con l'id specificata nell'url. Può essere eseguita solo da uno student.

- '/{teamId}/confirm' : (GET) Conferma la partecipazione al team indicato nell'uri. Se il team non esiste più viene laciata un'eccezione. Può essere eseguita solo da uno student.

- '/{teamId}/reject' : (GET) Rifiuta la partecipazione al team indicato nell'url. Può essere eseguita solo da uno student.

- '/{teamName}/vms' : (GET) Rende la lista delle VD associate al team sotto forma di DTO, solamente un teaacher può richiere tale informazione. Può essere eseguita solo da un teacher.

### VM  CONTROLLER ('API/controllers')

- '""': (PUT) Accetta come parametro un VMDTO, cambia i valori della vm (determinata dal campo id), con quelli ottenuti in input.

- '/{vmid}/{action}': (PUT) action può assumere i valori on/off. Serve per cambiare lo stato della vm indicata nell'url.

- '/{vmid}': (DELETE) elimina la vm indicata nell'url.

- '{vmId}/exec': (GET) rende l'immagine della vm indicata nell'url.


### TEACHER CONTROLLER ('API/teachers')

- '/{teacherId}/courses' (GET) Rende la lista dei corsi che possiede l'insegnante indicato nell'url.


## PAPER CONTROLLER ('API/papers)

- '/{paperId}/student': (GET) Rende ulo StudentDTO che ha generato il paper indicato nell'url. Può essere eseguita solo da un teacher.

- '/{paperId}/history': (GET) Rende lastoria del paper indicato nell'url sotto forma di una lista di DeliveredPaperDTO. Può essere eseguita solo da un teacher.

- '/{paperId}/delivery': (POST) Accetta come paramentro un ContentDTO, questo viene trasformato in un  DeliveredPaper e associato al paper indicato null'url. Questa è l'azione che fa lo studente per ineserire un paper.

- '/{paperId}/lastVersion': (GET) Rende l'ultima versione del paper indicato nell'url.

- '/{paperId}/check': (POST) Accetta come paramentro un ContentDTO, questo viene trasformato in un  DeliveredPaper e associato al paper indicato null'url. Questa è l'azione che fa il teacher per ineserirela correzione di un paper.


### ASSIGNMENT CONTROLLER ('API/assignments')

- '/{assignmentId}/papers': (GET) Rende la lista dei PaperDTO associati all'assignment indicato nell'url.

### COURE CONTROLLER ('/API/courses') 

- '/': (GET) Rende la lista di corsi.

- '/': (POST) Crea un nuovo corso con le informazioni passate nel body (vedi postman). Può essere esguito solo da un docente.

- '/{courseName}': (PUT) Aggiorna il corso indicato nella URL con le informazioni passate nel body (vedi postman). Può essere eseguito solo da un docente detentore del corso.

- '/{courseName}': (DELETE) Elimina il corso indicato nella URL. Può essere eseguito solo da un docente detentore del corso. 

- '/{courseName}/addTeacher': (POST) Aggiunge ai docenti del corso indicato nell'URL il docente il cui ID è passato nel body (vedi postman). Può essere eseguito solo da un docente detentore del corso.

- '/{courseName}/students': (GET) Prende la lista di studenti di un corso. Può essere eseguito solo da un docente detentore del corso o da studenti iscritti al corso.

- '/{courseName}/enrollOne': (POST) Iscrive al corso indicato nella URL lo studente con ID passato nel body (vedi postman). Può essere eseguito solo da un docente detentore del corso. 

- '/{courseName}/enrollMany': (POST) Iscrive al corso indicato nella URL gli studenti con ID passati nel body (vedi postman). Può essere eseguito solo da un docente detentore del corso. 

- '/{courseName}/disenrollOne': (POST) Disiscrive al corso indicato nella URL lo studente con ID passato nel body (vedi postman). Può essere eseguito solo da un docente detentore del corso. 

- '/{courseName}/disenrollMany': (POST) Disiscrive al corso indicato nella URL gli studenti con ID passati nel body (vedi postman). Può essere eseguito solo da un docente detentore del corso. 

- '/{courseName}/vmmodel': (GET) Prende il VM Model del corso indicato nella URL. Può essere eseguito solo da un docente detentore del corso.

- '/{courseName}/vmmodel': (POST) Setta il VM Model del corso indicato nella URL con le informazioni passate nel body (vedi postman). Può essere eseguito solo da un docente detentore del corso.

- '/{courseName}/enable': (GET) Abilità il corso. Può essere eseguito solo da un docente detentore del corso.

- '/{courseName}/disable': (GET) Disabilità il corso. Può essere eseguito solo da un docente detentore del corso.

- '/{courseName}/proposeTeam': (POST) Propone la formazione di un Team con le informazioni passate nel body (vedi postman) per il corso indicato nell'URL. Può essere eseguita solo da uno studente del corso ancora senza team. 

- '/{courseName}/team': (GET) Prende le informazioni del team del corso indicato nell'URL dell'utente che la esegue. Può essere eseguita solo da uno studente iscritto al corso. 

- '/{courseName}/teams': (GET) Prende le informazioni su tutti i team del corso indicato nell'URL. Può essere eseguito solo da un docente detentore del corso.

- '/{courseName}/freeStudents': (GET) Prende le informazioni sugli studenti ncora senza team nel iscritti al corso indicato nell'URL. Può essere eseguita soltanto da uno studente iscritto al corso e senza team. 

- '/{courseName}/proposedTeams': (GET) Prende le informazioni relative alle proposte di team per il corso indicato nell'URL. Può essere eseguita soltanto da uno studente iscritto al corso. 

- '/{courseName}/createVM': (POST) Crea un VM per il corso indicato nell'URL con le specifiche passte nel body (vedi postman). Può essere eseguita solo da uno studente iscritto al corso e con un team per quel corso. 

- '/{courseName}/vms' (GET) Prende tutte le VM del corso se eseguita dal docente o tutte le VM del team se eseguita dallo studente. 

- '/{courseName}/assignments' (GET) Prende tutti gli assignments del corso. 

- '/{coureName}/insertAssignments' (POST) Aggiunge un Assignment con le caratteristiche indicate nel body (vedi postman) al corso indicato nell'URL. Può essere eseguito solo da un docente detentore del corso.

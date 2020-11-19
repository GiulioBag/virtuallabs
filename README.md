# VISTE ESPOSTE AL CLIENT DIVICE PER CONTROLLER

TUTTE le seguenti viste devono essere validate e discusse da chi le implementa lato client.

### USER CONTROLLER ('API/user')

- '/signup' : (POST) Server per registrare un nuovo utente, per ora rende le informazioni inserite. Gli si deve passare un JSON del tipo: 
{
	"serialNumber":"s1",
	"lastName":"Bagnoli",
	"name":"Giulio",
	"email":"s1@studenti.polito.it",
    	"password":"Kioiki17"
}

- '/confirm/{tokenId}': (GET) Serve per confermare la email del'utente. E' accessibile dal link mandato per email. Qualunque altra azione fatta dall'utente prima della conferma del link viene respinta. Rende un bool a seconda ce il token passato sia corretto o meno.

- '/signin': (POST) Serve per far accedere l'utente al sito, gli si deve passare un JSON del tipo:
{
	"username":"s1",
	"password":"Kioiki17"
}

- '/signout': (GET) Serve per disabilitare il JWToken prima dello scadere del tempo, praticamente è il logout. COntrariamente alle altre viste si deve essere autenticati per effeettuarla -> nell'header deve essere presente un campo 'Authorization'.

### STUDENT CONTROLLER ('API/student')

#### Gestione Team

(Schermata Principale) 

- '/team/{courseName}' : (GET) rende Map <String, List<StudentInfoDTO>> che può essere vuota se per quel dato corso lo studente non appartiene a nessun team attivo, altrimenti contiene il nome del team e la lista dei suoi membri.

Se non si appartiene a nessun team per il corso nella parte superiore mostriamo il risultato di:

- '/possibleTeamMembeer/{courseNe}' : (GET) Dato un nome di un corso rende un lista di StudentDTO che non appartengono al nessun team per quel corso.

Magari mettiamo un bottone chiedere al server la creazione di un nuovo team:

- '/newTeam : (POST) Serve per registrare un nuovo team, non rende niente. Gli si deve passare un JSON, che nel codice corrisponde a un ProposedTeamDTO, del tipo:
{
	"name":"nomeTeam",
	"courseName":"NomeCorso",
	"studentIds": [ "id student", ... ],
	"timeout": "Date (java.util.Date)"
}

Nella parte inferiore della pagina mostriamo 

- 'proposedTeams/{courseName}' :(GET) Rende una lista contenente i DTO dei team proposti allo studente per il corso di interesse. Il creatore del team è il primo Student contenuto nel campo teams. Lo stato di adesione dei membri può essere dedotto in base alla loro lista di appartenenza: se si trovano in memebers allora hanno accettato, se si trovano in waitingmMambers devono acnora farlo.

- '/confirmTeam': (POST) Serve per confermare l'invito a un team, accetta come parametro il nome del team al quale vogliamo registrarci. Non rende niente.

- '/rejectTeam (POST) Serve per rifiutare l'invito a un team, accetta come parametro il nome del team del quale vOgliamo rifiutare l'INVITO. Non rende niente.


(PRESENTE  MA NONRICHIESTA)
- '/proposedTeams': (GET) Rende un mappa contenente come chiavi i nomi dei corsi per i quali abbiamo un invito pendente a un team e come valore un lista contente i DTO di tali team. 

#### Gestione Vm

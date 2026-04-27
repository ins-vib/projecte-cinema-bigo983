# Diagrames UML — CinemaDaw

## 1. Diagrama de Casos d'Ús

```plantuml
Vegeu casos-us.puml
```

**Actors:**
- **Visitant** — usuari no autenticat
- **CLIENT** — usuari registrat amb rol CLIENT
- **ADMIN** — usuari amb rol ADMIN

**Casos d'ús principals:**

| Actor | Cas d'ús |
|-------|----------|
| Visitant | Veure portada, Iniciar sessió, Registrar-se |
| CLIENT | Veure cartellera, Veure projeccions, Seleccionar seients, Comprar entrades, Veure entrades, Sol·licitar devolució, Tancar sessió |
| ADMIN | Gestionar cinemes/sales/seients/pel·lícules/projeccions, Gestionar devolucions, Veure cartellera, Tancar sessió |

**Relacions:**
- `Seleccionar seients` <<include>> `Veure projeccions`
- `Gestionar carret` <<include>> `Seleccionar seients`
- `Comprar entrades` <<include>> `Gestionar carret`
- `Sol·licitar devolució` <<include>> `Veure entrades`
- `Gestionar devolucions` <<extend>> `Sol·licitar devolució`

---

## 2. Diagrama de Classes UML

```plantuml
@startuml
class Cinema
class Room
class Seat
class SeatType <<enumeration>>
class Movie
class Screening
class Comanda
class Ticket
class TicketStatus <<enumeration>>
class User
class Role <<enumeration>>
class ReturnRequest
class ReturnStatus <<enumeration>>

Cinema "1" *-- "0..*" Room : té
Room "1" *-- "0..*" Seat : conté
Movie "1" *-- "0..*" Screening : programada en
Room "1" <-- "0..*" Screening : acull
Screening "1" --> "0..*" Ticket : genera
Seat "1" --> "0..*" Ticket : assignat a
Comanda "1" *-- "1..*" Ticket : conté
User "1" --> "0..*" Comanda : realitza
Ticket "1" --> "0..1" ReturnRequest : sol·licitada per
Seat ..> SeatType
Ticket ..> TicketStatus
User ..> Role
ReturnRequest ..> ReturnStatus

note bottom of Ticket
  Restricció única:
  (seat_id, screening_id)
end note

@enduml
```

---

## Fitxers PlantUML

Per renderitzar els diagrames `.puml` amb totes les classes i relacions:
- Instal·la l'extensió **PlantUML** a VS Code
- O utilitza [plantuml.com/plantuml](https://www.plantuml.com/plantuml/uml/)
- Fitxers: `docs/casos-us.puml` i `docs/classes.puml`

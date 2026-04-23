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

```mermaid
classDiagram
    class Cinema {
        -Long id
        -String cinemaName
        -String address
        -String city
        -Integer postalCode
    }
    class Room {
        -Long id
        -String name
        -int capacity
    }
    class Seat {
        -Long id
        -String seatRow
        -Integer number
        -Integer x
        -Integer y
        -boolean state
        -SeatType typeSeat
    }
    class SeatType {
        <<enumeration>>
        STANDARD
        VIP
        PREMIUM
        ADAPTED
    }
    class Movie {
        -Long id
        -String title
        -Integer duration
        -String genre
        -String description
        -LocalDate releaseDate
    }
    class Screening {
        -Long id
        -LocalDateTime dateTime
        -Double price
    }
    class Comanda {
        -Long id
        -LocalDateTime createdAt
    }
    class Ticket {
        -Long id
        -double price
        -TicketStatus status
    }
    class TicketStatus {
        <<enumeration>>
        ACTIVE
        RETURN_REQUESTED
        CANCELLED
    }
    class User {
        -Long id
        -String username
        -String password
        -Role role
    }
    class Role {
        <<enumeration>>
        ROLE_ADMIN
        ROLE_CLIENT
    }
    class ReturnRequest {
        -Long id
        -LocalDateTime requestedAt
        -ReturnStatus status
        -LocalDateTime resolvedAt
    }
    class ReturnStatus {
        <<enumeration>>
        PENDING
        CONFIRMED
        REJECTED
    }

    Cinema "1" *-- "0..*" Room : té
    Room "1" *-- "0..*" Seat : conté
    Movie "1" *-- "0..*" Screening : programada en
    Screening "1" --> "0..*" Ticket : genera
    Seat "1" --> "0..*" Ticket : assignat a
    Comanda "1" *-- "1..*" Ticket : conté
    User "1" --> "0..*" Comanda : realitza
    Ticket "1" --> "0..1" ReturnRequest : sol·licitada per
    Seat ..> SeatType
    Ticket ..> TicketStatus
    User ..> Role
    ReturnRequest ..> ReturnStatus
```

---

## Fitxers PlantUML

Per renderitzar els diagrames `.puml` amb totes les classes i relacions:
- Instal·la l'extensió **PlantUML** a VS Code
- O utilitza [plantuml.com/plantuml](https://www.plantuml.com/plantuml/uml/)
- Fitxers: `docs/casos-us.puml` i `docs/classes.puml`



**Usuaris de prova**

| Usuari | Contrasenya | Rol |
|--------|-------------|-----|
| `admin` | `admin` | ADMIN |
| `client` | `client` | CLIENT |

---

## Funcionalitat extra (reserves, carret i devolucions)

La funcionalitat extra implementada és el flux complet de compra d'entrades:

1. Selecció de seients en una projecció (`/screenings/reserve/{id}`).
2. Gestió de carret en sessió HTTP (`/cart`).
3. Checkout amb control de concurrència (`/cart/checkout`) i restricció única per seient+projecció.
4. Generació de comanda i tickets persistits a base de dades.
5. Historial d'entrades del client (`/tickets`).
6. Devolucions amb aprovació d'admin (`/admin/returns`).

Cancel·lació de reserva (via devolució):

- Client: `POST /tickets/{id}/return` crea `ReturnRequest` pendent.
- Admin confirma: ticket `CANCELLED` i seient alliberat (`seat.state = true`).
- Admin rebutja: ticket torna a `ACTIVE` i el seient es manté ocupat.

---

## Requisits d'avaluació coberts

| Requisit | Evidència resumida |
|---|---|
| Actors definits | Rols `ADMIN` i `CLIENT` amb permisos separats a Spring Security |
| Casos d'ús clau | Login, gestió CRUD, reserva, compra, devolució |
| Domini principal | `Cinema`, `Room`, `Seat`, `Movie`, `Screening`, `Comanda` (Order), `Ticket` |
| Relacions UML | Cardinalitats 1-N i N-1 representades al diagrama de classes |
| Coherència amb implementació | Entitats, repositoris i controladors alineats amb els diagrames |
| Flux extra multi-pas | Reserva → carret → compra → historial → devolució/aprovació |

---

## Fitxers de diagrames lliurats

- [docs/casos-us.puml](docs/casos-us.puml)
- [docs/classes.puml](docs/classes.puml)
- [docs/DIAGRAMES.md](docs/DIAGRAMES.md)

---

## Diagrama de Casos d'Ús

```mermaid
flowchart LR
    VISITANT(["Visitant"])
    CLIENT(["CLIENT"])
    ADMIN(["ADMIN"])

    subgraph pub ["Accés públic"]
        UC1("Veure portada")
        UC2("Iniciar sessió")
        UC3("Registrar-se")
    end

    subgraph cli ["Zona CLIENT"]
        UC0("Login")
        UC4("Veure cartellera")
        UC5("Veure detall pel·lícula")
        UC6("Veure projeccions")
        UC7("Seleccionar seients")
        UC8("Gestionar carret")
        UC9("Comprar entrades")
        UC10("Veure entrades comprades")
        UC11("Sol·licitar devolució")
    end

    subgraph adm ["Zona ADMIN"]
        UC12("Gestionar cinemes")
        UC13("Gestionar sales")
        UC14("Gestionar seients")
        UC15("Gestionar pel·lícules")
        UC16("Gestionar projeccions")
        UC17("Confirmar / Rebutjar devolucions")
        UC18("Tancar sessió")
    end

    VISITANT --> UC1 & UC2 & UC3

    CLIENT --> UC0 & UC4 & UC5 & UC6
    CLIENT --> UC7 --> UC8 --> UC9
    CLIENT --> UC10 --> UC11
    CLIENT --> UC18

    ADMIN --> UC0 & UC4 & UC5 & UC6
    ADMIN --> UC12 & UC13 & UC14 & UC15 & UC16
    ADMIN --> UC17
    ADMIN --> UC18

    UC7 -. "<<include>>" .-> UC6
    UC8 -. "<<include>>" .-> UC7
    UC9 -. "<<include>>" .-> UC8
    UC11 -. "<<include>>" .-> UC10
    UC17 -. "<<extend>>" .-> UC11
```

---

## Diagrama de Classes UML

```mermaid
classDiagram
    direction TB

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

# PROJECT CONTEXT — CinemaDaw (per a sessions futures)

> Aquest document és la guia ràpida i completa del projecte. Llegint això,
> qualsevol Claude pot continuar treballant sense haver d'explorar tot el codi.
> **Examen demà (2026-05-08):** s'haurà d'afegir una funcionalitat nova al projecte.

---

## 1. Stack tècnic

- **Java 17** + **Spring Boot 4.0.2** (parent `spring-boot-starter-parent`)
- **Spring MVC + Thymeleaf** (vistes server-side, no SPA)
- **Spring Data JPA + Hibernate**
- **H2 en memòria** (`jdbc:h2:mem:cinemadb`, console a `/h2-console`)
- **Spring Security** (form-login, BCrypt via `PasswordEncoderFactories.createDelegatingPasswordEncoder()`)
- **Bean Validation** (Jakarta Validation, `@NotBlank`, `@Size`, `@Min`, `@Max`, `@DecimalMin`...)
- **Maven** (`pom.xml`, `mvnw`, `mvnw.cmd`)
- **Port:** `8080`
- **Idioma UI/missatges:** Català
- **Comentaris codi:** Català/Castellà (informal, estil acadèmic)

`spring.jpa.hibernate.ddl-auto=create-drop` → cada arrencada crea taules netes;
`Proves` (CommandLineRunner) + `data.sql` carreguen dades inicials.

---

## 2. Estructura de paquets

```
com.daw.cinemadaw
 ├─ CinemadawApplication            # @SpringBootApplication
 ├─ config
 │   ├─ SecurityConfig              # filterChain + PasswordEncoder bean
 │   ├─ CustomLoginSuccessHandler   # Redirigeix segons rol (ADMIN→/admin, CLIENT→/client)
 │   ├─ SecurityModelAttributes     # @ControllerAdvice → afegeix isAuthenticated/isAdmin/isClient al model
 │   ├─ GlobalExceptionHandler      # @ControllerAdvice → 404, 403, 409, 400, 500 → "error" template
 │   └─ Proves                      # CommandLineRunner: crea cinemes/sales/seients/users de prova
 ├─ controller
 │   ├─ HomeController              # /, /home, /admin, /client, /login, /register, /admin/news
 │   ├─ CinemaController            # /cinemes, /cinema/{id}, /cinema/create|edit|update|delete, /services
 │   ├─ RoomController              # /room/{id}, /room/create|update, /room/{id}/update|delete
 │   ├─ SeatController              # /seat/{id} (mapa), /seats/{id} (edit), /seats/create/{roomId}, /seats/update/{id}, /seats/delete/{id}
 │   ├─ MovieController             # /movies/movies, /movies/create-movies, /movies/create|update|delete, /movies/detail/{id}
 │   ├─ ScreeningController         # /movies/projections/{id}, /screenings/{id}, /screenings/new|edit/{id}|update|delete/{id}, /screenings/reserve/{id} (GET+POST)
 │   ├─ CartController              # /cart (GET), /cart/checkout (POST), /cart/remove (POST)
 │   ├─ TicketController            # /tickets, /tickets/{id}/return, /admin/returns, /admin/returns/{id}/confirm|reject
 │   ├─ CookieController            # /cookies/* (demo)
 │   └─ SessionController           # /session/* (demo)
 ├─ domain.cinema
 │   ├─ Cinema, Room, Seat, SeatType (enum)
 │   ├─ Movie, Screening
 │   ├─ Comanda, Ticket, TicketStatus (enum)
 │   ├─ User, Role (enum)
 │   ├─ ReturnRequest, ReturnStatus (enum)
 │   └─ New (POJO, no entity — notícies a fitxer)
 ├─ repository
 │   └─ Cinema/Room/Seat/Movie/Screening/Ticket/Comanda/User/ReturnRequest Repository
 ├─ dto
 │   ├─ SeatsListDTO       (screeningId + List<Long> seatIds)
 │   ├─ CartScreeningDTO   (Screening + List<Seat> + Map<seatId, price> + totalPrice)
 │   └─ ServicesListDTO    (List<String> services — demo /services)
 └─ service
     ├─ TicketService            # getTicketsGroupedByOrder(), crearOrdreTickets() (transactional)
     ├─ NewService               # llegir/escriure news.txt al filesystem
     ├─ SeatLayoutService        # regenerateSeats(Room) → recrea seients en grid 10/fila
     ├─ SeatPricingUtils         # calculateTicketPrice(base, type) — STANDARD 0, PREMIUM +1.50, VIP +3.00
     └─ UserDetailService        # implements UserDetailsService → carrega User per username
```

---

## 3. Model de domini i relacions

```
Cinema 1 ─── 0..* Room  (cascade ALL, orphanRemoval)
Room   1 ─── 0..* Seat  (cascade ALL, orphanRemoval)
Movie  1 ─── 0..* Screening (cascade ALL, orphanRemoval)
Room   1 ←── 0..* Screening
Screening 1 ─── 0..* Ticket
Seat      1 ─── 0..* Ticket
Comanda   1 ─── 0..* Ticket (cascade ALL)
User      1 ─── 0..* Comanda
Ticket    1 ─── 0..* ReturnRequest
```

### Entitats — atributs clau

| Entitat | Camps |
|---|---|
| **Cinema** | id, cinemaName, address, city, postalCode, rooms |
| **Room** | id, name, capacity (int), cinema, seats |
| **Seat** | id, seatRow (String), number, x (0-79), y (0-59), state (boolean), typeSeat (SeatType), room — `@Table` UNIQUE no aplicat |
| **SeatType** | enum: STANDARD, PREMIUM, VIP, ADAPTED |
| **Movie** | id, title, duration, genre, description, releaseDate (LocalDate), screenings |
| **Screening** | id, dateTime (LocalDateTime), price (Double), movie, room |
| **Comanda** | id, createdAt, user, tickets |
| **Ticket** | id, price, status (TicketStatus), seat, screening, comanda — `@Table(uniqueConstraints = {seat_id, screening_id})` |
| **TicketStatus** | enum: ACTIVE, RETURN_REQUESTED, CANCELLED |
| **User** | id, username (unique), password (BCrypt), role (Role), comandes — `@Table(name="users")` (paraula reservada H2). `setRole(String)` parseja a Role. |
| **Role** | enum: ADMIN, CLIENT |
| **ReturnRequest** | id, ticket, requestedAt, status (PENDING/CONFIRMED/REJECTED), resolvedAt |

### Validacions actives (totes en català)
- `@NotBlank/@Size/@Min/@Max/@NotNull/@DecimalMin/@DecimalMax`
- Missatges com `"El nom del cinema és obligatori"`, `"El codi postal ha de ser entre 10000 i 99999"`...

---

## 4. Seguretat

`SecurityConfig.filterChain`:

- CSRF desactivat (per H2 console)
- Frame options desactivats (per H2 console)
- **Públic:** `/`, `/home`, `/login`, `/register`, `/css/**`, `/cookies/**`, `/session/**`, `/h2-console/**`
- **ADMIN-only:** `/admin/**`, `/cinemes/**`, `/cinema/**`, `/room/**`, `/seat/**`, `/seats/**`, `/movies/create-movies`, `/movies/create`, `/movies/update/**`, `/movies/update`, `/movies/delete/**`, `/screenings/new[/**]`, `/screenings/edit/**`, `/screenings/update`, `/screenings/delete/**`, `/admin/returns[/**]`
- **CLIENT-only:** `POST /screenings/reserve` (només CLIENT pot confirmar reserva)
- **ADMIN+CLIENT:** `/movies/movies`, `/movies/detail/**`, `/movies/projections/**`, `/screenings/reserve/**`, `/screenings/*`, `/cart[/**]`, `/tickets`, `/tickets/*/return`
- Login form: `/login` (custom), success handler redirigeix segons rol
- Logout: `/logout` → `/login?logout`

`PasswordEncoder = createDelegatingPasswordEncoder()` (accepta `{bcrypt}`, `{noop}`, etc.).

`SecurityModelAttributes` exposa al model totes les vistes:
- `${isAuthenticated}`, `${isAdmin}`, `${isClient}`

`UserDetailService.loadUserByUsername` → `org.springframework.security.core.userdetails.User` amb `roles(role.name())` (Spring Security afegeix `ROLE_` prefix automàticament).

---

## 5. Usuaris de prova (creats a `Proves.run()`)

| Usuari | Contrasenya | Rol |
|--------|-------------|-----|
| `admin` | `admin` | ADMIN |
| `client` | `client` | CLIENT |

També es poden registrar nous usuaris via `/register` (sempre rol CLIENT).

---

## 6. Flux de compra (funcionalitat extra)

1. **CLIENT** entra a `/movies/movies` → tria pel·lícula → `/movies/projections/{id}` → tria projecció → `/screenings/reserve/{id}` (mapa SVG de seients).
2. Selecciona seients (JS pinta vermell `#e50914` el seleccionat). Submit POST `/screenings/reserve` → guarda a `session.cart` (List<SeatsListDTO>) — **suporta multi-projecció**.
3. `/cart` mostra resum amb preus calculats per `SeatPricingUtils` (STANDARD base, PREMIUM +1.50, VIP +3.00).
4. POST `/cart/checkout` → crea `Comanda`, `Ticket(status=ACTIVE)` per cada seient, `seat.state=false`. **Constraint únic** (seat+screening) evita doble venda — `DataIntegrityViolationException` capturada → missatge "Seients ja comprats".
5. `/tickets` mostra l'historial agrupat per Comanda (servei `TicketService.getTicketsGroupedByOrder`).
6. Client → POST `/tickets/{id}/return` → crea `ReturnRequest(status=PENDING)`, `ticket.status=RETURN_REQUESTED`.
7. ADMIN → `/admin/returns` veu pendents. POST `/admin/returns/{id}/confirm` → `ticket.status=CANCELLED`, `seat.state=true`. POST `/admin/returns/{id}/reject` → `ticket.status=ACTIVE` un altre cop.

---

## 7. Convencions de codi i UI

### Backend
- Controller-pattern clàssic: `@Controller` + Thymeleaf, no `@RestController`.
- Constructor injection (sense Lombok).
- Camps `private` (mai `final` als entities — JPA needs setters).
- Cada acció té GET (form/llistat) + POST (acció). Responen amb `redirect:/...` + `RedirectAttributes` (flash `successMessage`/`errorMessage`).
- `@Valid @ModelAttribute` + `BindingResult` per validar; si `hasErrors()` → tornar al form.
- `Optional<T>` amb `isPresent()/isEmpty()` per cerca per ID.
- Comentaris explicatius (estil acadèmic). **No els eliminis** — formen part de l'estil del projecte.

### Frontend (Thymeleaf)
- Totes les pàgines ús fragment `~{layout :: layout(~{::content})}`.
- `layout.html` mostra navbar segons `${isAdmin}/${isClient}/${isAuthenticated}`.
- Patró estructural per pàgina:
  ```html
  <div class="page-header">
    <div>
      <h2>Títol</h2>
      <div class="breadcrumb">...</div>
    </div>
    <div>… botons d'acció …</div>
  </div>
  ```
- Forms: `class="form-page"` > `class="form-card"` > `<form>` amb `class="form-field"` per camp.
- Errors per camp: `<div th:if="${#fields.hasErrors('camp')}" th:errors="*{camp}" class="form-error"></div>`.
- Llistats: `<table>` amb `<th>` + `<td>`; eliminar amb `<form>` POST inline + `confirm()`.
- Estats buits: `<div class="empty-state">...</div>`.
- Badges: `badge`, `badge-success`, `badge-warning`, `badge-info`, `badge-muted`, `badge-accent`, `badge-seat-VIP/PREMIUM/STANDARD/ADAPTED`.
- Botons: `btn btn-primary`, `btn btn-secondary`, `btn btn-ghost`, `btn btn-danger`, `btn btn-sm`.
- Action links a taules: `action-link view|edit|primary|delete`.

### CSS (`/static/css/style.css`, ~1080 línies, dark theme)
Variables `:root`:
```css
--bg:#0f1115  --bg-soft:#151821  --surface:#1a1e29  --surface-hover:#202535
--text:#e7e9ee  --text-soft:#c7cad3  --muted:#8b90a0
--border:#262b38  --border-strong:#353a4a
--primary:#e50914 (Netflix red)  --primary-hover:#ff1a25
--accent:#f5c518 (IMDb yellow)
--success:#2fbf71  --warning:#f0a33b  --danger:#e34c4c  --info:#3b82f6
--content-max:1180px  --radius:10px  --radius-sm:6px
```
Colors per tipus de seient (hard-codejats):
- STANDARD `#2fbf71` (verd)
- PREMIUM `#a855f7` (lila)
- VIP `#f5c518` (groc)
- ADAPTED `#3b82f6` (blau)
- Ocupat `#5a5f6c` (gris)
- Seleccionat `#e50914` (vermell, només a la reserva)

---

## 8. Endpoints (referència ràpida)

### Públic
- `GET /` → redirect `/home`
- `GET /home` → home.html
- `GET /login`, `POST /login` (Spring Security)
- `GET /register`, `POST /register`
- `GET /h2-console/**`

### ADMIN
- `GET /admin` → admin/home
- `GET /admin/news`, `POST /admin/news`
- **Cinemes:** `GET /cinemes`, `GET /cinema/{id}`, `GET /cinema/create`, `POST /cinema/create`, `GET /cinema/edit/{id}`, `POST /cinema/update`, `POST /cinema/delete/{id}`
- **Sales:** `GET /room/create?cinemaId={id}`, `POST /room/create`, `GET /room/{id}`, `GET /room/{id}/update`, `POST /room/update`, `POST /room/{id}/delete`
- **Seients (mapa+CRUD):** `GET /seat/{roomId}`, `GET /seats/create/{roomId}`, `POST /seats/create/{roomId}`, `GET /seats/{id}`, `POST /seats/update/{id}`, `POST /seats/delete/{id}`
- **Pel·lícules:** `GET /movies/create-movies`, `POST /movies/create`, `GET /movies/update/{id}`, `POST /movies/update`, `POST /movies/delete/{id}`
- **Projeccions:** `GET /screenings/new?movieId={id}`, `POST /screenings/new`, `GET /screenings/edit/{id}`, `POST /screenings/update`, `POST /screenings/delete/{id}`
- **Devolucions:** `GET /admin/returns`, `POST /admin/returns/{id}/confirm`, `POST /admin/returns/{id}/reject`

### CLIENT (i alguns ADMIN+CLIENT)
- `GET /client` → cartellera + notícies
- `GET /movies/movies` → llistat (admin veu botons CRUD)
- `GET /movies/detail/{id}`, `GET /movies/projections/{id}`
- `GET /screenings/{id}` (detall projecció)
- `GET /screenings/reserve/{id}` (mapa SVG; admin pot editar seients, client pot reservar)
- `POST /screenings/reserve` (només CLIENT) → afegeix al carret
- `GET /cart`, `POST /cart/checkout`, `POST /cart/remove`
- `GET /tickets`, `POST /tickets/{id}/return`

### Demos
- `/services` (form de checkboxes), `/cookies/**`, `/session/**`

---

## 9. Plantilles Thymeleaf (mapping URL → fitxer)

| Vista | Fitxer |
|---|---|
| home | `templates/home.html` |
| admin home | `templates/admin/home.html` |
| client home | `templates/client/home.html` |
| login | `templates/login.html` |
| register | `templates/register.html` |
| layout (fragment) | `templates/layout.html` |
| error | `templates/error.html` |
| Cinemes llistat | `templates/cinemes/cinemes.html` |
| Cinema detall | `templates/cinemes/detail-cinema.html` |
| Cinema crear/editar | `templates/cinemes/create-cinemes.html` / `edit-cinema.html` |
| Sala detall/crear/editar | `templates/room/detail-room.html` / `create-room.html` / `edit-room.html` |
| Mapa de seients | `templates/seat/detail-seat.html` |
| Seient crear/editar/eliminar | `templates/seat/create-seat.html` / `change-seat.html` / `delete-seat.html` |
| Pel·lícules | `templates/movies/movies.html` / `detail-movies.html` / `create-movies.html` / `edit-movies.html` |
| Projeccions | `templates/projections/Screening.html` / `ScreeningDetail.html` / `ScreeningNew.html` / `ScreeningEdit.html` / `ScreeningReserve.html` |
| Carret | `templates/cart/cart.html` |
| Entrades del client | `templates/tickets/tickets.html` |
| Devolucions admin | `templates/admin/returns.html` |
| Notícies admin | `templates/admin/news.html` |

---

## 10. Patró per **afegir una nova funcionalitat** (preparació examen)

Si demà demanen afegir, p.ex., **una entitat nova X** amb CRUD complet:

### Passos canònics

1. **Entity** (`domain/cinema/X.java`):
   - `@Entity`, `@Id @GeneratedValue(IDENTITY)`, `@Column`, validacions Jakarta amb missatges en català.
   - Constructor buit (per JPA) + constructor amb args.
   - Getters/setters + `toString()`.
   - Relacions: `@ManyToOne` / `@OneToMany(mappedBy=, cascade=CascadeType.ALL, orphanRemoval=true)`.

2. **Repository** (`repository/XRepository.java`):
   - `extends JpaRepository<X, Long>` + `@Repository`.
   - Mètodes derivats si calen: `findByCamp(Tipus)`, `findByXAndY(...)`...

3. **Controller** (`controller/XController.java`):
   - `@Controller`, constructor injection.
   - GET llistat → `model.addAttribute("xs", repo.findAll())` → return `"x/x"`.
   - GET detall, GET create-form, POST create, GET edit-form, POST update, POST delete.
   - Sempre `@Valid @ModelAttribute X x, BindingResult result, RedirectAttributes ra`.
   - `if (result.hasErrors()) return "x/create-x";`
   - `redirect:/...` + flash messages (`successMessage`/`errorMessage`).

4. **Templates** (`templates/x/*.html`):
   - `x.html` (llistat amb taula), `detail-x.html`, `create-x.html`, `edit-x.html`.
   - Tots usen `~{layout :: layout(~{::content})}`.
   - Patró `page-header > breadcrumb`, `form-page > form-card > form-field`, taula, `confirm()` als delete.

5. **SecurityConfig**: afegir `requestMatchers("/x/**").hasRole("ADMIN")` (o el rol que toqui).

6. **Layout/navbar**: afegir nou enllaç `<a class="nav-btn" th:if="${isAdmin}" th:href="@{/x}">X</a>`.

7. **Diagrames** (`docs/casos-us.puml`, `docs/classes.puml`, README.md mermaid): actualitzar.

### Si la funcionalitat és **una acció sobre entitat existent** (ex: "exportar a CSV", "afegir favorits", "ratings", "comentaris"...)

- Afegir un nou camp a l'entity o nova entitat petita.
- Nou endpoint al controller corresponent (o nou controller).
- Plantilla nova o modificació de l'existent.

---

## 11. Coses que **podrien demanar a l'examen** (especulació)

Possibles funcionalitats noves (totes encaixen amb el patró):

- **Sistema de favorits** (User → List<Movie>): nova entitat `Favorite(user, movie)` o relació `@ManyToMany`. Botó cor a la pàgina de detall pel·lícula.
- **Valoracions/Reviews** (User+Movie → estrelles+comentari): entitat `Review` amb cardinality CLIENT 1-N Movie 1-N.
- **Cupons/descomptes** aplicables al carret: entitat `Coupon(code, discountPercent, validFrom, validUntil)`. POST `/cart/coupon` afegeix codi a sessió.
- **Estadístiques admin** (`/admin/stats`): pàgina amb gràfics — total entrades venudes, ingressos per pel·lícula/cinema/mes.
- **Cerca/filtres a cartellera**: paràmetres GET (`?genre=Sci-Fi&dateFrom=...`).
- **Notificacions per a l'usuari**: entitat `Notification(user, message, read)` + dropdown al navbar.
- **Comentaris de notícies / valoracions de notícies**.
- **Reserva de butaques múltiples sales en una mateixa compra** (ja parcialment implementat).
- **Promocions**: pel·lícula 2x1 segons dia de la setmana — afegir camp `promotion` a Screening o regla a `SeatPricingUtils`.
- **Gestió de personal** (entitat Employee amb relació Cinema).
- **Catering/snacks** afegits al checkout.
- **Mode fosc/clar toggle** (només frontend).
- **Internacionalització i18n** (Spring Messages).

### Si demanen una **entitat completament nova amb CRUD**

Plantilla minimal — segueix els 7 passos del §10. Tot el patró està replicat a Cinema/Room/Seat/Movie. Trieu-ne un com a referència segons compleixitat (Cinema = simple, Screening = amb selectors).

---

## 12. Fitxers de diagrames del repo

- `plant.uml` (PlantUML — diagrama de classes, font de veritat)
- `docs/casos-us.puml`
- `docs/classes.puml`
- `docs/DIAGRAMES.md`
- `README.md` conté els diagrames Mermaid renderitzats per a la rúbrica de UML.

> **CLAUDE.md** del projecte només conté la rúbrica d'avaluació dels diagrames (no instruccions tècniques).

---

## 13. Estat actual / commits recents

```
825dbc5 Fix seats layout, support multi-screening cart, and update UML docs
5630668 Merge branch 'main'
f957993 24/04
c5cee8a Update README.md
3fba49d recorrecio readme
```

Branca actual: `main`. Sense canvis pendents.

`AVALUACIO.md` estima 127–130/132 punts (96–98%). Tots els CRUD complets, validacions en català, seguretat per rol, devolucions amb workflow d'admin.

---

## 14. Comandes útils

```powershell
# Arrencar (Windows)
.\mvnw.cmd spring-boot:run

# Compilar
.\mvnw.cmd clean package

# H2 console (un cop arrencat)
http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:cinemadb · user: sa · pass: (buit)
```

---

## 15. Què fer demà quan arribi la consigna de l'examen

1. **Llegeix la consigna sencera** abans de tocar res.
2. Decideix si és **entitat nova amb CRUD** o **acció sobre entitat existent**.
3. Segueix el patró de §10 (entity → repo → controller → templates → security → navbar → diagrames).
4. Reusa l'estil CSS i el `layout`. No creïs un disseny nou.
5. Posa **validacions amb missatges en català**.
6. Afegeix **flash messages** als POST.
7. Si toca seguretat, **afegeix la regla a `SecurityConfig`** abans de provar.
8. Actualitza `plant.uml` / mermaid del README amb la nova entitat/relació.
9. Prova-ho amb `admin/admin` i `client/client` segons els permisos.
10. Commit final amb missatge clar de què s'ha afegit.

> **No reescriguis l'estil dels comentaris**, no eliminis comentaris existents,
> no canviïs el format dels missatges (català). Mantingues coherència total
> amb la resta del projecte.

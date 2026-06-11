# Report

Arthantyo (S6469361), Harry (S5876095), Mantia (S123123)

## Introduction

Our program is a Lord of the Rings battle simulator and map editor. The user
builds a graph of locations (nodes) and routes (edges), then adds armies. Each
army belongs to one of five factions, split across two teams. Nodes and edges
can also hold events. When the map is ready, the user runs the simulation one
step at a time: armies move along the edges, fight a battle whenever two teams
meet, and may trigger a random event. The user can also undo and redo edits, and
save the whole state to a JSON file.

## Program design

The program uses MVC, with each role in its own package.

**Model** (`model`). `Graph` is the main model class. It holds the `Node`s and
`Edge`s and all the editing methods (add/remove nodes, edges, armies, events).
`Node` and `Edge` share the `BattleLocation`/`Nameable` parent classes, so
anything that can hold armies and events is handled the same way. An `Army` has
a `Faction`, which gives it a `Team`, and holds `Unit`s. Battle logic sits in
`Battle`, `BattleResolver` and `BattleResult`, so the battle rules can be changed
on their own. `Event` is an interface with three versions
(`ReinforcementsEvent`, `NaturalDisasterEvent`, `HiddenWeaponryEvent`), so adding
a new event is easy. `Simulation` runs the time steps.

**View** (`view`). `MainFrame` is the window with the toolbar and a `JSplitPane`.
`GraphPanel` draws the graph and armies, and `OptionsPanel` shows the selected
node or edge. The view only reads the model, it never changes it.

**Controller** (`controller`). This turns user input into model changes.
`GraphMouseListener` handles selecting, dragging, panning and zooming. Edits are
wrapped in `Command` objects and run through a `CommandManager` that keeps an
undo and a redo stack. We chose the Command pattern mainly for this: undo/redo
(2.6) comes almost for free, since each command knows how to reverse itself.

**Observer.** The view has to react to model changes without the model knowing
about the view. We wrote our own observer in `Graph`: listeners register a
`Consumer` for a `GraphEventType` (e.g. `NODE_ADDED`, `SELECTION_CHANGED`,
`ARMIES_CHANGED`), and the model `emit`s the event after each change.
`GraphPanel` and `OptionsPanel` subscribe and refresh themselves. Using an enum
per event type (instead of one generic "changed" event) lets each view react
only to what it cares about.

**JSON export (2.7).** We split this into `GraphSerializer` (walks the graph and
decides *what* to write) and `JsonBuilder` (a small hand-written writer for the
*how*: indentation, commas, escaping). `GraphSaver` connects it to a
`JFileChooser` and forces a `.json` ending. No libraries are used.

## Evaluation of the program

The program is stable and does everything Parts 1 and 2 ask for. The editor
(adding, removing, selecting and moving nodes and edges, plus panning and the
optional zoom), the army and event handling, the full multi-phase simulation
step, battle resolution, undo/redo and JSON export all work as expected. We did
not run into any bugs in normal use.

One design choice worth explaining is how undo/redo and the simulation work
together. Edit commands change the live model objects, but the simulation needs
to step forward and back through whole graph states. So `Simulation` keeps a
history of deep-copied `Graph` snapshots, and we clear the command history when a
simulation starts or ends. This stops the two systems from holding on to each
other's stale objects. The cost is copying the graph each step, which is fine for
the map sizes here.

The main thing that works well is the separation of concerns. Adding a new event
type or a different battle rule only touches one class, and the per-event
listeners keep the view code small. Splitting the JSON serialiser from the writer
also made the indentation (the part the README warns about) easy to get right.

There are a few limitations. Events show up as separate `JOptionPane` popups,
which gets annoying when one step triggers many of them; a combat log panel would
be nicer. The JSON export also leaves out node positions and unit ability/history
on purpose, since loading is not required, but those would be needed to load a save
back. With more time we would add loading from JSON, smarter army pathing, and
replace the debug `System.out.println` calls with real logging.

Overall we are confident the program is correct, and the design makes it easy to
extend, which is what the assignment cared about most.

## Questions

Please answer the following questions:

1. In this assignment, the program should follow the Model View Controller (MVC) pattern. Please explain the design of the program in terms of the MVC pattern. Specifically try to answer the following questions:
   - MVC consists of three components: Model, view and controller. Can you please explain the role of each component? Please provide examples of these roles from the assignment. How are these three roles (i.e. Model, view and controller) are implemented in the assignment?
   - MVC enforces special constraints on the dependencies between its three components: Model, view and controller. Please explain these constraints, and why are they important?

---

Answer:

**Roles of each component.**

- *Model* holds the data and logic and knows nothing about how it is shown. This
  is our `model` package: `Graph`, `Node`, `Edge`, `Army`, `Unit`, `Event`, the
  battle classes and `Simulation`. For example, `Graph.deleteNode` removes a node
  and its edges and then notifies its observers; it does not draw anything.
- *View* shows the model and has no logic of its own. This is the `view` package.
  `GraphPanel` reads the graph and paints the nodes, edges and armies, and
  `OptionsPanel` shows the selected node or edge. It observes the model and
  refreshes when told.
- *Controller* takes user input and changes the model. This is the `controller`
  package plus the action listeners in the view. `GraphMouseListener` handles
  clicks and drags, and the toolbar buttons make `Command` objects that the
  `CommandManager` runs on the `Graph`.

**Dependency constraints and why they matter.**

The main rule is that the **model must not depend on the view or controller**.
The model is observable, but it only knows its observers as plain listeners
(`Consumer`s per `GraphEventType`), not as actual UI classes. The view and
controller are allowed to depend on the model: the view reads it, the controller
changes it.

This matters because it keeps the model independent and reusable: the same model
could run under a different UI, or be tested with no UI at all. It also gives one
clear direction of flow: the controller changes the model, the model notifies the
view, the view redraws. That avoids two-way dependencies and makes the program
easier to follow and extend.

---

2. The Swing library provides the ability to create nested user interface components. In this assignment, you created multiple JPanel components on the user interface. These contain other user interface components to build-up a tree of user interface components.
   Which design pattern does Swing implement to create a hierarchy of user interface components? Please explain this pattern and how it is implemented in Swing.

---

Answer:

---

3. The Observer pattern is useful to implement the MVC pattern. Can you please explain the relationship between the Observer pattern and the MVC pattern?
   Please provide an example from the assignment on how the Observer pattern supports implementing the MVC pattern.

---

Answer:

---

## Process evaluation

We agreed on the package layout (model/view/controller) before writing much code,
and that paid off: since the model never touched the view, new features rarely
forced rewrites. These clear boundaries also let us work in parallel without many
conflicts. The hardest parts were the simulation's multi-phase step (getting
battles and events to fire at the right moments without processing an army twice)
and the JSON indentation, which we fixed by splitting the *what* from the *how*
in the serialiser. The main thing we learned is how much a clean MVC design and
the Command pattern simplify otherwise awkward features like undo/redo.

## Conclusions

We built a full map editor and battle simulator that meets all the Part 1 and 2
requirements. A clean MVC structure, our own observer mechanism and the Command
pattern kept the code modular and made each feature (armies, events, battles,
undo/redo and JSON export) easy to add on a stable base.

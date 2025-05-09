###🧾 Product Requirements Document (PRD)
##Project Overview
We're building a native Android application that allows 4 players to play the traditional Mendikot card game. The app handles game setup, team formation, card dealing, gameplay logic (including trump selection), round resolution, and tracking of round history.

###Technology Stack

Language: Kotlin
IDE: Android Studio
Architecture: MVVM
UI Framework: Jetpack Compose
Navigation: Jetpack Navigation Component
Dependency Injection: Hilt
Database: Room (optional, for score/history persistence)
Networking: Retrofit (reserved for future online play)
Concurrency: Kotlin Coroutines
Image Loading: Coil
Design: Material Design
Animation: Lottie
View Handling: ViewModel + LiveData
Build Tools: Gradle
Themes: Material + Custom XML

#####Core Functionalities & Flow

####Step 1: App Initialization & Splash Screen

Display animated splash screen (using Lottie)
Navigate to home screen after delay

####Step 2: Home Screen

UI Components:

Button: "Start New Game"


Behavior:

On click, navigate to Player Setup screen



####Step 3: Player Setup Screen

UI Components:

Input field: Human player name
Display: Three bot names auto-filled
Button: "Proceed"


Behavior:

Store names and navigate to Game Screen



####Step 4: Game Setup Logic

Shuffle and distribute 13 cards per player (5 + 4 + 4 format)
Player to dealer's right selects one card from hand and places it face-down as trump
Store the trump card and exclude it from the hand until revealed
Determine first dealer randomly; after that, follow win-based rules for next dealer

####Step 5: Game Screen Layout

Display table layout (4 players at 4 corners)
Show human player's hand at bottom
Bot hands shown as card backs
Indicate player turns with highlights or arrows
Provide access to trick history and scoreboard

####Step 6: Trick Playing Logic

Player to dealer's right leads the first trick
Follow-suit rule enforced
If player has no card in the led suit:

Option to reveal trump (only once per round)
If trump is revealed:

Trump suit is now active for all future tricks
Trump card added back to owner's hand
Player who revealed trump must play a trump if they have one




Determine trick winner:

If no trump played: highest card of led suit wins
If trump played: highest trump wins



####Step 7: Bot AI

Basic rule-following bot logic:

Follow suit when possible
If can't follow suit, consider trumping
Attempt to win tens
Work with partner (avoid over-trumping if partner is winning)
Request trump only if beneficial



####Step 8: Trick and Round Tracking

Maintain trick count for each team
Track 10s won by each team
Detect special wins:

Mendikot (all 4 tens)
Whitewash (all 13 tricks)


Decide round winner:

3 or 4 tens = win
2-2 tens: 7+ tricks = win



####Step 9: Scoreboard and UI Feedback

Show live:

Team scores (tricks and tens)
Trump suit (after revealed)
Current trick leader


After round ends:

Show winning team
Show type of win (regular, mendikot, whitewash)
Determine next dealer



####Step 10: Game Loop

After each round, allow player to:

Start new round
Return to home screen


###📁 Project Structure
Minimal but maintainable structure following MVVM + Hilt + Compose guidelines.
src/
└── main/
    ├── java/com/mendikot/
    │   ├── MainActivity.kt                  # Navigation host
    │   ├── di/
    │   │   └── AppModule.kt                 # Hilt DI config
    │   ├── data/
    │   │   ├── models/
    │   │   │   ├── Player.kt                # Player name, team, position
    │   │   │   └── GameState.kt             # Deck, current turn, scores
    │   │   └── repository/
    │   │       └── GameRepository.kt        # Business logic and state
    │   ├── ui/
    │   │   ├── screens/
    │   │   │   ├── HomeScreen.kt
    │   │   │   ├── PlayerSetupScreen.kt 
    │   │   │   ├── TrumpSelectionScreen.kt
    │   │   │   ├── GameScreen.kt
    │   │   │   └── ResultScreen.kt
    │   │   └── components/
    │   │       └── PlayingCard.kt           # Composable card view
    │   ├── viewmodel/
    │   │   └── GameViewModel.kt             # Exposes state to UI
    │   └── navigation/
    │       └── NavGraph.kt                  # Compose Navigation
    └── res/
        ├── values/
        │   ├── strings.xml
        │   ├── colors.xml
        │   └── themes.xml
        ├── drawable/                        # Card backs, UI icons
        └── raw/                             # (Optional) Sound effects or animations
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

Animated Lottie splash screen
Delayed navigation to Home Screen

####Step 2: Home Screen
UI Components:

"Start New Game" button

Behavior:

On click → navigate to Game Mode Selection Screen

####Step 3: Game Mode Selection Screen
UI Components:

Radio or card-style options:

🧍 Single Player (1 human + 3 bots)
🤝 Manual 4 Player (local multiplayer)



Behavior:

On selection → navigate to Player Setup Screen with appropriate form

####Step 4: Player Setup Screen
Single Player Mode:

Input field: Human player name
3 Bot names auto-filled
Button: "Proceed"

Manual 4-Player Mode:

Input fields: Player 1, 2, 3, 4
Optional team selection (drag-and-drop or auto-assign)
Button: "Proceed"

Behavior:

Store player names → navigate to Game Screen

####Step 5: Game Setup Logic
Common to both modes:

Random dealer selection (initial round only)
Cards dealt in 5 + 4 + 4 format (total 13)
Player to dealer's right selects one face-down trump card from their hand

Manual Mode:

During trump selection, show only that player's hand on the screen
Human manually selects the trump card → system hides it until revealed
System prompts pass-and-play message after trump selection

####Step 6: Game Screen Layout
Single Player Mode:

Show human hand (bottom)
Bot hands as card backs
Highlight current turn

Manual Mode:

Show only the current player's cards
Cards are hidden until their turn
Display "Pass device to Player X" prompt between turns

####Step 7: Trick Playing Logic (Both Modes)

Player to dealer's right starts
Must follow suit if possible
If unable to follow:

May reveal trump (once per round)
Trump card is added back to hand upon reveal



Trick winner rules:

Highest of lead suit wins unless trump played
Highest trump wins if any are played

####Step 8: Bot AI (Only for Single Player)

Follow suit, trump strategy, avoid wasting high cards
Avoid beating partner's winning card
Attempt to collect 10s strategically

####Step 9: Trick & Round Tracking

Maintain per-team count of:

Tricks won
Tens won


Detect:

Mendikot: All four 10s captured
Whitewash: All 13 tricks captured



Round Win Logic:

Team with 3 or 4 tens wins
If 2-2 tens → team with 7+ tricks wins

####Step 10: Scoreboard & Feedback
Live during game:

Team trick/10s count
Trump suit (once revealed)
Current leader of trick

After round ends:

Winning team name
Win type (regular, mendikot, whitewash)
Prompt for next round or home return

####Step 11: Game Loop
After each round, provide:
"Close"



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
    │   │   │   ├── ModeSelectionScreen.kt
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
public class DebugEngine {
    
    private static boolean debugMode = true;
    
    // Enable or disable debug output
    public static void setDebugMode(boolean mode) {
        debugMode = mode;
    }
    
    // Log a message
    public static void log(String message) {
        if (debugMode) {
            System.out.println("[DEBUG] " + message);
        }
    }
    
    // Print the entire game state
    public static void printGameState(GameBackend game) {
        if (!debugMode) return;
        
        System.out.println("\n===== GAME STATE =====");
        
        // Player team
        System.out.println("YOUR TEAM:");
        GameBackend.Character[] players = game.getPlayerTeam();
        for (int i = 0; i < players.length; i++) {
            printCharacter(players[i], i + 1);
        }
        
        // Enemy team
        System.out.println("\nENEMY TEAM:");
        GameBackend.Character[] enemies = game.getEnemyTeam();
        for (int i = 0; i < enemies.length; i++) {
            printCharacter(enemies[i], i + 1);
        }
        
        // Game status
        System.out.println("\nGAME STATUS:");
        System.out.println("  Game Over: " + game.isGameOver());
        if (game.isGameOver()) {
            System.out.println("  Winner: " + (game.didPlayerWin() ? "PLAYER" : "ENEMY"));
        }
        
        System.out.println("======================\n");
    }
    
    // Print a single character's stats
    private static void printCharacter(GameBackend.Character c, int number) {
        System.out.println("  [" + number + "] " + c.name);
        System.out.println("      HP: " + c.currentHp + "/" + c.maxHp);
        System.out.println("      Speed: " + c.speed);
        System.out.println("      Attack: " + c.attack);
        System.out.println("      Ult Charge: " + c.ultCharge + "/" + c.ultMax);
        System.out.println("      Item: " + (c.item.equals("") ? "None" : c.item));
        System.out.println("      Status: " + (c.isAlive ? "ALIVE" : "DEFEATED"));
        
        if (c.shieldActive) System.out.println("      [SHIELD ACTIVE]");
        if (c.knifeBoostTurns > 0) System.out.println("      [KNIFE BOOST: " + c.knifeBoostTurns + " turns]");
        if (c.poisonTurns > 0) System.out.println("      [POISONED: " + c.poisonTurns + " turns]");
    }
    
    // Quick status check
    public static void quickStatus(GameBackend game) {
        if (!debugMode) return;
        
        GameBackend.Character[] players = game.getPlayerTeam();
        GameBackend.Character[] enemies = game.getEnemyTeam();
        
        System.out.print("[STATUS] Players: ");
        for (int i = 0; i < players.length; i++) {
            System.out.print(players[i].name + "(" + players[i].currentHp + "HP) ");
        }
        
        System.out.print(" | Enemies: ");
        for (int i = 0; i < enemies.length; i++) {
            System.out.print(enemies[i].name + "(" + enemies[i].currentHp + "HP) ");
        }
        System.out.println();
    }
    
    // Log error messages
    public static void error(String message) {
        System.err.println("[ERROR] " + message);
    }
    
    // Log warning messages
    public static void warn(String message) {
        if (debugMode) {
            System.out.println("[WARN] " + message);
        }
    }
}

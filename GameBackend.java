import java.util.Random;

public class GameBackend {
    
    // Character data
    public static class Character {
        public String name;
        public int maxHp;
        public int currentHp;
        public int speed;
        public int attack;
        public int ultCharge;
        public int ultMax;
        public String ultName;
        public String item;
        public boolean isAlive;
        public boolean shieldActive;
        public int knifeBoostTurns;
        public int poisonTurns;
        
        public Character(String name, int hp, int speed, int attack, int ultMax, String ultName) {
            this.name = name;
            this.maxHp = hp;
            this.currentHp = hp;
            this.speed = speed;
            this.attack = attack;
            this.ultMax = ultMax;
            this.ultName = ultName;
            this.ultCharge = 0;
            this.isAlive = true;
            this.shieldActive = false;
            this.knifeBoostTurns = 0;
            this.poisonTurns = 0;
            this.item = "";
        }
    }
    
    // Game state
    private Character[] playerTeam;
    private Character[] enemyTeam;
    private Random rand;
    private String battleLog;
    private boolean gameOver;
    private boolean playerWon;
    
    public GameBackend() {
        rand = new Random();
        battleLog = "";
        gameOver = false;
        playerWon = false;
    }
    
    // Create teams after character selection
    public void createTeams(int char1Index, int char2Index) {
        // Character templates: 0=Knight, 1=Robot, 2=Witch
        Character[] templates = {
            new Character("Knight", 250, 2, 30, 2, "Counter"),
            new Character("Robot", 300, 1, 35, 3, "Rocket"),
            new Character("Witch", 200, 3, 20, 5, "Revive")
        };
        
        // Create player team
        playerTeam = new Character[2];
        playerTeam[0] = copyCharacter(templates[char1Index]);
        playerTeam[0].name = "O's " + playerTeam[0].name;
        playerTeam[1] = copyCharacter(templates[char2Index]);
        playerTeam[1].name = "O's " + playerTeam[1].name;
        
        // Create enemy team (random)
        enemyTeam = new Character[2];
        int e1 = rand.nextInt(3);
        int e2 = rand.nextInt(3);
        while (e2 == e1) e2 = rand.nextInt(3);
        enemyTeam[0] = copyCharacter(templates[e1]);
        enemyTeam[0].name = "X's " + enemyTeam[0].name;
        enemyTeam[1] = copyCharacter(templates[e2]);
        enemyTeam[1].name = "X's " + enemyTeam[1].name;
    }
    
    private Character copyCharacter(Character c) {
        return new Character(c.name, c.maxHp, c.speed, c.attack, c.ultMax, c.ultName);
    }
    
    // Equip items
    public void equipItem(int playerIndex, int itemIndex) {
        String[] items = {"Shield", "Potion", "Knife", "Boots", "Blow Dart"};
        playerTeam[playerIndex].item = items[itemIndex];
        
        // Boots give permanent speed boost
        if (items[itemIndex].equals("Boots")) {
            playerTeam[playerIndex].speed += 2;
        }
    }
    
    // Get methods
    public Character[] getPlayerTeam() { return playerTeam; }
    public Character[] getEnemyTeam() { return enemyTeam; }
    public String getBattleLog() { return battleLog; }
    public boolean isGameOver() { return gameOver; }
    public boolean didPlayerWin() { return playerWon; }
    
    // Player attacks enemy
    public void playerAttack(int playerIndex, int enemyIndex) {
        battleLog = "";
        Character attacker = playerTeam[playerIndex];
        Character target = enemyTeam[enemyIndex];
        
        if (!attacker.isAlive || !target.isAlive) {
            battleLog = "Invalid target!";
            return;
        }
        
        performAttack(attacker, target);
        enemyTurn();
        endTurn();
    }
    
    // Player uses ultimate
    public void playerUltimate(int playerIndex) {
        battleLog = "";
        Character user = playerTeam[playerIndex];
        
        if (!user.isAlive || user.ultCharge < user.ultMax) {
            battleLog = "Cannot use ultimate!";
            return;
        }
        
        useUltimate(user, playerTeam, enemyTeam);
        user.ultCharge = 0;
        
        enemyTurn();
        endTurn();
    }
    
    // Player uses item
    public void playerUseItem(int playerIndex) {
        battleLog = "";
        Character user = playerTeam[playerIndex];
        
        if (!user.isAlive || user.item.equals("")) {
            battleLog = "No item to use!";
            return;
        }
        
        useItem(user);
        enemyTurn();
        endTurn();
    }
    
    // Perform an attack
    private void performAttack(Character attacker, Character target) {
        int damage = attacker.attack;
        
        // Knife boost
        if (attacker.knifeBoostTurns > 0) {
            damage = (int)(damage * 1.5);
        }
        
        // Shield blocks
        if (target.shieldActive) {
            battleLog += target.name + " blocked with shield!\n";
            target.shieldActive = false;
            return;
        }
        
        target.currentHp -= damage;
        battleLog += attacker.name + " attacks " + target.name + " for " + damage + " damage!\n";
        
        // Witch poison
        if (attacker.name.contains("Witch")) {
            target.poisonTurns = 3;
            battleLog += target.name + " is poisoned!\n";
        }
        
        checkDeath(target);
    }
    
    // Use ultimate ability
    private void useUltimate(Character user, Character[] allies, Character[] enemies) {
        battleLog += user.name + " uses " + user.ultName + "!\n";
        
        if (user.name.contains("Knight")) {
            user.shieldActive = true;
            battleLog += user.name + " raises shield!\n";
            
        } else if (user.name.contains("Robot")) {
            for (Character e : enemies) {
                if (e.isAlive) {
                    e.currentHp -= 50;
                    battleLog += "Rocket hits " + e.name + " for 50 damage!\n";
                    checkDeath(e);
                }
            }
            
        } else if (user.name.contains("Witch")) {
            // Try to revive dead ally
            boolean revived = false;
            for (Character ally : allies) {
                if (!ally.isAlive) {
                    ally.currentHp = 50;
                    ally.isAlive = true;
                    battleLog += ally.name + " revived with 50 HP!\n";
                    revived = true;
                    break;
                }
            }
            // If no dead allies, heal living one
            if (!revived) {
                for (Character ally : allies) {
                    if (ally.isAlive && ally != user) {
                        ally.currentHp += 20;
                        if (ally.currentHp > ally.maxHp) ally.currentHp = ally.maxHp;
                        battleLog += ally.name + " healed for 20 HP!\n";
                        return;
                    }
                }
            }
        }
    }
    
    // Use item
    private void useItem(Character user) {
        battleLog += user.name + " uses " + user.item + "!\n";
        
        if (user.item.equals("Potion")) {
            user.currentHp += 40;
            if (user.currentHp > user.maxHp) user.currentHp = user.maxHp;
            battleLog += user.name + " heals 40 HP!\n";
            user.item = "";
            
        } else if (user.item.equals("Shield")) {
            user.shieldActive = true;
            battleLog += user.name + " raises shield!\n";
            user.item = "";
            
        } else if (user.item.equals("Knife")) {
            user.knifeBoostTurns = 2;
            battleLog += user.name + " gets +50% damage for 2 turns!\n";
            user.item = "";
        }
    }
    
    // AI turn
    private void enemyTurn() {
        for (Character enemy : enemyTeam) {
            if (!enemy.isAlive) continue;
            
            // Simple AI: attack random alive player
            Character target = null;
            if (playerTeam[0].isAlive && playerTeam[1].isAlive) {
                target = playerTeam[rand.nextInt(2)];
            } else if (playerTeam[0].isAlive) {
                target = playerTeam[0];
            } else if (playerTeam[1].isAlive) {
                target = playerTeam[1];
            }
            
            if (target != null) {
                // 30% chance to use ult if available
                if (enemy.ultCharge >= enemy.ultMax && rand.nextInt(100) < 30) {
                    useUltimate(enemy, enemyTeam, playerTeam);
                    enemy.ultCharge = 0;
                } else {
                    performAttack(enemy, target);
                }
            }
        }
    }
    
    // End of turn processing
    private void endTurn() {
        // Process poison
        for (Character c : playerTeam) {
            if (c.poisonTurns > 0) {
                c.currentHp -= 10;
                battleLog += c.name + " takes 10 poison damage!\n";
                c.poisonTurns--;
                checkDeath(c);
            }
        }
        for (Character c : enemyTeam) {
            if (c.poisonTurns > 0) {
                c.currentHp -= 10;
                battleLog += c.name + " takes 10 poison damage!\n";
                c.poisonTurns--;
                checkDeath(c);
            }
        }
        
        // Decrease knife boost
        for (Character c : playerTeam) {
            if (c.knifeBoostTurns > 0) c.knifeBoostTurns--;
        }
        for (Character c : enemyTeam) {
            if (c.knifeBoostTurns > 0) c.knifeBoostTurns--;
        }
        
        // Charge ultimates
        for (Character c : playerTeam) {
            if (c.isAlive && c.ultCharge < c.ultMax) c.ultCharge++;
        }
        for (Character c : enemyTeam) {
            if (c.isAlive && c.ultCharge < c.ultMax) c.ultCharge++;
        }
        
        // Check game over
        checkGameOver();
    }
    
    private void checkDeath(Character c) {
        if (c.currentHp <= 0) {
            c.currentHp = 0;
            c.isAlive = false;
            battleLog += c.name + " has been defeated!\n";
        }
    }
    
    private void checkGameOver() {
        boolean playerAlive = playerTeam[0].isAlive || playerTeam[1].isAlive;
        boolean enemyAlive = enemyTeam[0].isAlive || enemyTeam[1].isAlive;
        
        if (!playerAlive) {
            gameOver = true;
            playerWon = false;
            battleLog += "\n*** GAME OVER - You Lost! ***\n";
        } else if (!enemyAlive) {
            gameOver = true;
            playerWon = true;
            battleLog += "\n*** VICTORY - You Won! ***\n";
        }
    }
}

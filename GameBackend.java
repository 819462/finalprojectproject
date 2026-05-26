public class GameBackend 
{
    private Character[] playerTeam;
    private Character[] enemyTeam;
    private String battleLog;
    private boolean gameOver;
    private boolean playerWon;
    
    public GameBackend() 
    {
        battleLog = "";
        gameOver = false;
        playerWon = false;
    }
    
    public void createTeams(int char1Index, int char2Index) 
    {
        Character knight = new Character("Knight", 250, 2, 30, 2, "Counter");
        Character robot = new Character("Robot", 300, 1, 35, 3, "Rocket");
        Character witch = new Character("Witch", 200, 3, 20, 5, "Revive");
        
        playerTeam = new Character[2];
        
        if (char1Index == 0)
        {
            playerTeam[0] = copyCharacter(knight);
        }
        else if (char1Index == 1)
        {
            playerTeam[0] = copyCharacter(robot);
        }
        else
        {
            playerTeam[0] = copyCharacter(witch);
        }
        playerTeam[0].name = "O's " + playerTeam[0].name;
        
        if (char2Index == 0)
        {
            playerTeam[1] = copyCharacter(knight);
        }
        else if (char2Index == 1)
        {
            playerTeam[1] = copyCharacter(robot);
        }
        else
        {
            playerTeam[1] = copyCharacter(witch);
        }
        playerTeam[1].name = "O's " + playerTeam[1].name;
        
        enemyTeam = new Character[2];
        
        int firstEnemyChoice = (int)(Math.random() * 3);
        int secondEnemyChoice = (int)(Math.random() * 3);
        
        while (secondEnemyChoice == firstEnemyChoice)
        {
            secondEnemyChoice = (int)(Math.random() * 3);
        }
        
        if (firstEnemyChoice == 0)
        {
            enemyTeam[0] = copyCharacter(knight);
        }
        else if (firstEnemyChoice == 1)
        {
            enemyTeam[0] = copyCharacter(robot);
        }
        else
        {
            enemyTeam[0] = copyCharacter(witch);
        }
        enemyTeam[0].name = "X's " + enemyTeam[0].name;
        
        if (secondEnemyChoice == 0)
        {
            enemyTeam[1] = copyCharacter(knight);
        }
        else if (secondEnemyChoice == 1)
        {
            enemyTeam[1] = copyCharacter(robot);
        }
        else
        {
            enemyTeam[1] = copyCharacter(witch);
        }
        enemyTeam[1].name = "X's " + enemyTeam[1].name;
    }
    
    private Character copyCharacter(Character c) 
    {
        return new Character(c.name, c.maxHp, c.speed, c.attack, c.ultMax, c.ultName);
    }
    
    public void equipItem(int playerIndex, int itemIndex) 
    {
        String[] items = {"Shield", "Potion", "Knife", "Boots", "Blow Dart"};
        playerTeam[playerIndex].item = items[itemIndex];
        
        if (items[itemIndex].equals("Boots")) 
        {
            playerTeam[playerIndex].speed += 2;
        }
    }
    
    public Character[] getPlayerTeam() 
    { 
        return playerTeam; 
    }
    
    public Character[] getEnemyTeam() 
    { 
        return enemyTeam; 
    }
    
    public String getBattleLog() 
    { 
        return battleLog; 
    }
    
    public boolean isGameOver() 
    { 
        return gameOver; 
    }
    
    public boolean didPlayerWin() 
    { 
        return playerWon; 
    }
    
    public void playerAttack(int playerIndex, int enemyIndex) 
    {
        battleLog = "";
        Character attacker = playerTeam[playerIndex];
        Character target = enemyTeam[enemyIndex];
        
        if (!attacker.isAlive || !target.isAlive) 
        {
            battleLog = "Invalid target!";
            return;
        }
        
        performAttack(attacker, target);
        enemyTurn();
        endTurn();
    }
    
    public void playerUltimate(int playerIndex) 
    {
        battleLog = "";
        Character user = playerTeam[playerIndex];
        
        if (!user.isAlive || user.ultCharge < user.ultMax) 
        {
            battleLog = "Cannot use ultimate!";
            return;
        }
        
        useUltimate(user, playerTeam, enemyTeam);
        user.ultCharge = 0;
        
        enemyTurn();
        endTurn();
    }
    
    public void playerUseItem(int playerIndex) 
    {
        battleLog = "";
        Character user = playerTeam[playerIndex];
        
        if (!user.isAlive || user.item.equals("")) 
        {
            battleLog = "No item to use!";
            return;
        }
        
        useItem(user);
        enemyTurn();
        endTurn();
    }
    
    private void performAttack(Character attacker, Character target) 
    {
        int damage = attacker.attack;
        
        if (attacker.knifeBoostTurns > 0) 
        {
            damage = (int)(damage * 1.5);
        }
        
        if (target.shieldActive) 
        {
            battleLog += target.name + " blocked with shield!\n";
            target.shieldActive = false;
            return;
        }
        
        target.currentHp -= damage;
        battleLog += attacker.name + " attacks " + target.name + " for " + damage + " damage!\n";
        
        if (attacker.name.contains("Witch")) 
        {
            target.poisonTurns = 3;
            battleLog += target.name + " is poisoned!\n";
        }
        
        checkDeath(target);
    }
    
    private void useUltimate(Character user, Character[] allies, Character[] enemies) 
    {
        battleLog += user.name + " uses " + user.ultName + "!\n";
        
        if (user.name.contains("Knight")) 
        {
            user.shieldActive = true;
            battleLog += user.name + " raises shield!\n";
            
        } 
        else if (user.name.contains("Robot")) 
        {
            int i = 0;
            while (i < enemies.length)
            {
                if (enemies[i].isAlive) 
                {
                    enemies[i].currentHp -= 50;
                    battleLog += "Rocket hits " + enemies[i].name + " for 50 damage!\n";
                    checkDeath(enemies[i]);
                }
                i++;
            }
            
        } 
        else if (user.name.contains("Witch")) 
        {
            boolean revived = false;
            int i = 0;
            while (i < allies.length)
            {
                if (!allies[i].isAlive) 
                {
                    allies[i].currentHp = 50;
                    allies[i].isAlive = true;
                    battleLog += allies[i].name + " revived with 50 HP!\n";
                    revived = true;
                    break;
                }
                i++;
            }
            
            if (!revived) 
            {
                int j = 0;
                while (j < allies.length)
                {
                    if (allies[j].isAlive && allies[j] != user) 
                    {
                        allies[j].currentHp += 20;
                        if (allies[j].currentHp > allies[j].maxHp) 
                        {
                            allies[j].currentHp = allies[j].maxHp;
                        }
                        battleLog += allies[j].name + " healed for 20 HP!\n";
                        return;
                    }
                    j++;
                }
            }
        }
    }
    
    private void useItem(Character user) 
    {
        battleLog += user.name + " uses " + user.item + "!\n";
        
        if (user.item.equals("Potion")) 
        {
            user.currentHp += 40;
            if (user.currentHp > user.maxHp) 
            {
                user.currentHp = user.maxHp;
            }
            battleLog += user.name + " heals 40 HP!\n";
            user.item = "";
            
        } 
        else if (user.item.equals("Shield")) 
        {
            user.shieldActive = true;
            battleLog += user.name + " raises shield!\n";
            user.item = "";
            
        } 
        else if (user.item.equals("Knife")) 
        {
            user.knifeBoostTurns = 2;
            battleLog += user.name + " gets +50% damage for 2 turns!\n";
            user.item = "";
        }
    }
    
    private void enemyTurn() 
    {
        int i = 0;
        while (i < enemyTeam.length)
        {
            if (!enemyTeam[i].isAlive) 
            {
                i++;
                continue;
            }
            
            Character target = null;
            if (playerTeam[0].isAlive && playerTeam[1].isAlive) 
            {
                target = playerTeam[(int)(Math.random() * 2)];
            } 
            else if (playerTeam[0].isAlive) 
            {
                target = playerTeam[0];
            } 
            else if (playerTeam[1].isAlive) 
            {
                target = playerTeam[1];
            }
            
            if (target != null) 
            {
                if (enemyTeam[i].ultCharge >= enemyTeam[i].ultMax && (int)(Math.random() * 100) < 30) 
                {
                    useUltimate(enemyTeam[i], enemyTeam, playerTeam);
                    enemyTeam[i].ultCharge = 0;
                } 
                else 
                {
                    performAttack(enemyTeam[i], target);
                }
            }
            i++;
        }
    }
    
    private void endTurn() 
    {
        int i = 0;
        while (i < playerTeam.length)
        {
            if (playerTeam[i].poisonTurns > 0) 
            {
                playerTeam[i].currentHp -= 10;
                battleLog += playerTeam[i].name + " takes 10 poison damage!\n";
                playerTeam[i].poisonTurns--;
                checkDeath(playerTeam[i]);
            }
            i++;
        }
        
        int j = 0;
        while (j < enemyTeam.length)
        {
            if (enemyTeam[j].poisonTurns > 0) 
            {
                enemyTeam[j].currentHp -= 10;
                battleLog += enemyTeam[j].name + " takes 10 poison damage!\n";
                enemyTeam[j].poisonTurns--;
                checkDeath(enemyTeam[j]);
            }
            j++;
        }
        
        int k = 0;
        while (k < playerTeam.length)
        {
            if (playerTeam[k].knifeBoostTurns > 0) 
            {
                playerTeam[k].knifeBoostTurns--;
            }
            k++;
        }
        
        int m = 0;
        while (m < enemyTeam.length)
        {
            if (enemyTeam[m].knifeBoostTurns > 0) 
            {
                enemyTeam[m].knifeBoostTurns--;
            }
            m++;
        }
        
        int n = 0;
        while (n < playerTeam.length)
        {
            if (playerTeam[n].isAlive && playerTeam[n].ultCharge < playerTeam[n].ultMax) 
            {
                playerTeam[n].ultCharge++;
            }
            n++;
        }
        
        int p = 0;
        while (p < enemyTeam.length)
        {
            if (enemyTeam[p].isAlive && enemyTeam[p].ultCharge < enemyTeam[p].ultMax) 
            {
                enemyTeam[p].ultCharge++;
            }
            p++;
        }
        
        checkGameOver();
    }
    
    private void checkDeath(Character c) 
    {
        if (c.currentHp <= 0) 
        {
            c.currentHp = 0;
            c.isAlive = false;
            battleLog += c.name + " has been defeated!\n";
        }
    }
    
    private void checkGameOver() 
    {
        boolean playerAlive = playerTeam[0].isAlive || playerTeam[1].isAlive;
        boolean enemyAlive = enemyTeam[0].isAlive || enemyTeam[1].isAlive;
        
        if (!playerAlive) 
        {
            gameOver = true;
            playerWon = false;
            battleLog += "\n*** GAME OVER - You Lost! ***\n";
        } 
        else if (!enemyAlive) 
        {
            gameOver = true;
            playerWon = true;
            battleLog += "\n*** VICTORY - You Won! ***\n";
        }
    }
}

import java.util.Random;

public class GameBackend 
{
    
    public static class Character 
    {
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
        
        public Character(String name, int hp, int speed, int attack, int ultMax, String ultName) 
        {
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
    
    private Character[] myTeam;
    private Character[] badGuys;
    private Random rng;
    private String log;
    private boolean done;
    private boolean won;
    private int turnCounter;
    
    public GameBackend() 
    {
        rng = new Random();
        log = "";
        done = false;
        won = false;
        turnCounter = 0;
    }
    
    public void createTeams(int char1Index, int char2Index) 
    {
        Character[] options = 
        {
            new Character("Knight", 250, 2, 30, 2, "Counter"),
            new Character("Robot", 300, 1, 35, 3, "Rocket"),
            new Character("Witch", 200, 3, 20, 5, "Revive")
        };
        
        myTeam = new Character[2];
        myTeam[0] = cloneChar(options[char1Index]);
        myTeam[0].name = "O's " + myTeam[0].name;
        myTeam[1] = cloneChar(options[char2Index]);
        myTeam[1].name = "O's " + myTeam[1].name;
        
        badGuys = new Character[2];
        int e1 = rng.nextInt(3);
        int e2 = rng.nextInt(3);
        while (e2 == e1) e2 = rng.nextInt(3);
        badGuys[0] = cloneChar(options[e1]);
        badGuys[0].name = "X's " + badGuys[0].name;
        badGuys[1] = cloneChar(options[e2]);
        badGuys[1].name = "X's " + badGuys[1].name;
    }
    
    private Character cloneChar(Character c) 
    {
        return new Character(c.name, c.maxHp, c.speed, c.attack, c.ultMax, c.ultName);
    }
    
    public void equipItem(int playerIndex, int itemIndex) 
    {
        String[] stuff = {"Shield", "Potion", "Knife", "Boots", "Blow Dart"};
        myTeam[playerIndex].item = stuff[itemIndex];
        
        if (stuff[itemIndex].equals("Boots")) 
        {
            myTeam[playerIndex].speed += 2;
        }
    }
    
    public Character[] getPlayerTeam() { return myTeam; }
    public Character[] getEnemyTeam() { return badGuys; }
    public String getBattleLog() { return log; }
    public boolean isGameOver() { return done; }
    public boolean didPlayerWin() { return won; }
    
    public void playerAttack(int playerIndex, int enemyIndex) 
    {
        log = "";
        Character attacker = myTeam[playerIndex];
        Character target = badGuys[enemyIndex];
        
        if (!attacker.isAlive || !target.isAlive) 
        {
            log = "Invalid target!";
            return;
        }
        
        doAttack(attacker, target);
        turnCounter++;
        
        checkWinner();
        if (done) return;
        
        if (turnCounter >= 2) 
        {
            badGuysTurn();
            checkWinner();
            if (done) return;
            processTurn();
            turnCounter = 0;
        }
    }
    
    public void playerUltimate(int playerIndex) 
    {
        log = "";
        Character user = myTeam[playerIndex];
        
        if (!user.isAlive || user.ultCharge < user.ultMax) 
        {
            log = "Cannot use ultimate!";
            return;
        }
        
        doUltimate(user, myTeam, badGuys);
        user.ultCharge = 0;
        turnCounter++;
        
        checkWinner();
        if (done) return;
        
        if (turnCounter >= 2) 
        {
            badGuysTurn();
            checkWinner();
            if (done) return;
            processTurn();
            turnCounter = 0;
        }
    }
    
    public void playerUseItem(int playerIndex) 
    {
        log = "";
        Character user = myTeam[playerIndex];
        
        if (!user.isAlive || user.item.equals("")) 
        {
            log = "No item to use!";
            return;
        }
        
        activateItem(user);
        turnCounter++;
        
        checkWinner();
        if (done) return;
        
        if (turnCounter >= 2) 
        {
            badGuysTurn();
            checkWinner();
            if (done) return;
            processTurn();
            turnCounter = 0;
        }
    }
    
    private void doAttack(Character attacker, Character target) 
    {
        int dmg = attacker.attack;
        
        if (attacker.knifeBoostTurns > 0) 
        {
            dmg = (int)(dmg * 1.5);
        }
        
        if (target.shieldActive) 
        {
            log += target.name + " blocked with shield!\n";
            target.shieldActive = false;
            return;
        }
        
        target.currentHp -= dmg;
        log += attacker.name + " attacks " + target.name + " for " + dmg + " damage!\n";
        
        if (attacker.name.contains("Witch")) 
        {
            target.poisonTurns = 3;
            log += target.name + " is poisoned!\n";
        }
        
        checkIfDead(target);
    }
    
    private void doUltimate(Character user, Character[] friends, Character[] enemies) 
    {
        log += user.name + " uses " + user.ultName + "!\n";
        
        if (user.name.contains("Knight")) 
        {
            user.shieldActive = true;
            log += user.name + " raises shield!\n";
            
        } 
        else if (user.name.contains("Robot")) 
        {
            for (Character e : enemies) 
            {
                if (e.isAlive) 
                {
                    e.currentHp -= 50;
                    log += "Rocket hits " + e.name + " for 50 damage!\n";
                    checkIfDead(e);
                }
            }
            
        } 
        else if (user.name.contains("Witch")) 
        {
            boolean revived = false;
            for (Character buddy : friends) 
            {
                if (!buddy.isAlive) 
                {
                    buddy.currentHp = 50;
                    buddy.isAlive = true;
                    log += buddy.name + " revived with 50 HP!\n";
                    revived = true;
                    break;
                }
            }
            if (!revived) 
            {
                for (Character buddy : friends) 
                {
                    if (buddy.isAlive && buddy != user) 
                    {
                        buddy.currentHp += 20;
                        if (buddy.currentHp > buddy.maxHp) buddy.currentHp = buddy.maxHp;
                        log += buddy.name + " healed for 20 HP!\n";
                        return;
                    }
                }
            }
        }
    }
    
    private void activateItem(Character user) 
    {
        log += user.name + " uses " + user.item + "!\n";
        
        if (user.item.equals("Potion")) 
        {
            user.currentHp += 40;
            if (user.currentHp > user.maxHp) user.currentHp = user.maxHp;
            log += user.name + " heals 40 HP!\n";
            user.item = "";
            
        } 
        else if (user.item.equals("Shield")) 
        {
            user.shieldActive = true;
            log += user.name + " raises shield!\n";
            user.item = "";
            
        } 
        else if (user.item.equals("Knife")) 
        {
            user.knifeBoostTurns = 2;
            log += user.name + " gets +50% damage for 2 turns!\n";
            user.item = "";
        }
    }
    
    private void badGuysTurn() 
    {
        for (Character enemy : badGuys) 
        {
            if (!enemy.isAlive) continue;
            
            Character target = null;
            if (myTeam[0].isAlive && myTeam[1].isAlive) 
            {
                target = myTeam[rng.nextInt(2)];
            } 
            else if (myTeam[0].isAlive) 
            {
                target = myTeam[0];
            } 
            else if (myTeam[1].isAlive) 
            {
                target = myTeam[1];
            }
            
            if (target != null) 
            {
                if (enemy.ultCharge >= enemy.ultMax && rng.nextInt(100) < 30) 
                {
                    doUltimate(enemy, badGuys, myTeam);
                    enemy.ultCharge = 0;
                } 
                else 
                {
                    doAttack(enemy, target);
                }
            }
        }
    }
    
    private void processTurn() 
    {
        for (Character c : myTeam) 
        {
            if (c.poisonTurns > 0) 
            {
                c.currentHp -= 10;
                log += c.name + " takes 10 poison damage!\n";
                c.poisonTurns--;
                checkIfDead(c);
            }
        }
        for (Character c : badGuys) 
        {
            if (c.poisonTurns > 0) 
            {
                c.currentHp -= 10;
                log += c.name + " takes 10 poison damage!\n";
                c.poisonTurns--;
                checkIfDead(c);
            }
        }
        
        for (Character c : myTeam) 
        {
            if (c.knifeBoostTurns > 0) c.knifeBoostTurns--;
        }
        for (Character c : badGuys) 
        {
            if (c.knifeBoostTurns > 0) c.knifeBoostTurns--;
        }
        
        for (Character c : myTeam) 
        {
            if (c.isAlive && c.ultCharge < c.ultMax) c.ultCharge++;
        }
        for (Character c : badGuys) 
        {
            if (c.isAlive && c.ultCharge < c.ultMax) c.ultCharge++;
        }
        
        checkWinner();
    }
    
    private void checkIfDead(Character c) 
    {
        if (c.currentHp <= 0) 
        {
            c.currentHp = 0;
            c.isAlive = false;
            log += c.name + " has been defeated!\n";
        }
    }
    
    private void checkWinner() 
    {
        boolean playerStillAlive = myTeam[0].isAlive || myTeam[1].isAlive;
        boolean enemyStillAlive = badGuys[0].isAlive || badGuys[1].isAlive;
        
        if (!playerStillAlive) 
        {
            done = true;
            won = false;
            log += "\n*** GAME OVER - You Lost! ***\n";
        } 
        else if (!enemyStillAlive) 
        {
            done = true;
            won = true;
            log += "\n*** VICTORY - You Won! ***\n";
        }
    }
}

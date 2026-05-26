public class Character 
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

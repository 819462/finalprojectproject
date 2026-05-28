import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class GameFrontend extends JFrame 
{
    
    private GameBackend game;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private int pickedChar1 = -1;
    private int pickedChar2 = -1;
    private String[] charNames = {"Knight", "Robot", "Witch"};
    private String[] charInfo = 
    {
        "HP:250 Speed:2 Atk:30 Ult:Counter",
        "HP:300 Speed:1 Atk:35 Ult:Rocket", 
        "HP:200 Speed:3 Atk:20 Ult:Revive"
    };
    
    private int pickedItem1 = -1;
    private int pickedItem2 = -1;
    private String[] itemNames = {"Shield", "Potion", "Knife", "Boots", "Blow Dart"};
    private String[] itemInfo = 
    {
        "Blocks all damage once",
        "Heals 40 HP",
        "+50% damage for 2 turns",
        "+2 Speed permanently",
        "Poison enemy (3 uses)"
    };
    
    private JProgressBar[] goodGuyHpBars;
    private JProgressBar[] badGuyHpBars;
    private JLabel[] goodGuyLabels;
    private JLabel[] badGuyLabels;
    private JButton[] button;
    private JLabel charPickStatus;
    private JLabel itemPickStatus;
    private JLabel endGameLabel;
    
    public GameFrontend() 
    {
        super("Combat Game - Without MSG Guaranteed");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        game = new GameBackend();
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        mainPanel.add(makeStartScreen(), "START");
        mainPanel.add(makeCharPickScreen(), "CHAR_SELECT");
        mainPanel.add(makeItemPickScreen(), "ITEM_SELECT");
        mainPanel.add(makeBattleScreen(), "BATTLE");
        mainPanel.add(makeEndScreen(), "GAMEOVER");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "START");
        
        setVisible(true);
    }
    
    private JPanel makeStartScreen() 
    {
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.BLACK);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(10, 10, 10, 10);
        
        JLabel title = new JLabel("Combat Game");
        title.setFont(new Font("Arial", Font.BOLD, 48));
        title.setForeground(Color.YELLOW);
        c.gridy = 0;
        panel.add(title, c);
        
        JLabel subtitle = new JLabel("Oliver - Sophia - Ryan");
        subtitle.setFont(new Font("Arial", Font.ITALIC, 20));
        subtitle.setForeground(Color.CYAN);
        c.gridy = 1;
        panel.add(subtitle, c);
        
        JButton playButton = new JButton("PLAY");
        playButton.setFont(new Font("Arial", Font.BOLD, 30));
        playButton.setPreferredSize(new Dimension(200, 60));
        playButton.addActionListener(e -> 
        {
            pickedChar1 = -1;
            pickedChar2 = -1;
            cardLayout.show(mainPanel, "CHAR_SELECT");
        });
        c.gridy = 2;
        panel.add(playButton, c);
        
        return panel;
        
    }
    
    private JPanel makeCharPickScreen() 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        
        JLabel title = new JLabel("Choose Your Characters", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);
        
        JPanel charPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        charPanel.setBackground(new Color(30, 30, 30));
        charPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        
        for (int i = 0; i < 3; i++) 
        {
            final int charIndex = i;
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            row.setBackground(new Color(50, 50, 50));
            
            JButton btn = new JButton(charNames[i]);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.setPreferredSize(new Dimension(120, 50));
            btn.addActionListener(e -> pickChar(charIndex));
            
            JLabel desc = new JLabel(charInfo[i]);
            desc.setFont(new Font("Arial", Font.PLAIN, 14));
            desc.setForeground(Color.LIGHT_GRAY);
            
            row.add(btn);
            row.add(desc);
            charPanel.add(row);
        }
        
        panel.add(charPanel, BorderLayout.CENTER);
        
        charPickStatus = new JLabel("Select Character 1", SwingConstants.CENTER);
        charPickStatus.setFont(new Font("Arial", Font.BOLD, 16));
        charPickStatus.setForeground(Color.YELLOW);
        charPickStatus.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(charPickStatus, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void pickChar(int index) 
    {
        
        if (pickedChar1 == -1) 
        {
            
            pickedChar1 = index;
            
            charPickStatus.setText("Select Character 2 (different from " + charNames[index] + ")");
            
        } 
        else if (pickedChar2 == -1 && index != pickedChar1) 
        {
            
            pickedChar2 = index;
            
            game.createTeams(pickedChar1, pickedChar2);
            cardLayout.show(mainPanel, "ITEM_SELECT");
            
        }
    }
    
    private JPanel makeItemPickScreen() 
    {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        
        JLabel title = new JLabel("Choose Items", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(title, BorderLayout.NORTH);
        
        JPanel itemPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        itemPanel.setBackground(new Color(30, 30, 30));
        itemPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        
        for (int i = 0; i < 5; i++) 
        {
            final int itemIndex = i;
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            row.setBackground(new Color(50, 50, 50));
            
            JButton btn = new JButton(itemNames[i]);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setPreferredSize(new Dimension(120, 40));
            btn.addActionListener(e -> pickItem(itemIndex));
            
            JLabel desc = new JLabel(itemInfo[i]);
            desc.setFont(new Font("Arial", Font.PLAIN, 14));
            desc.setForeground(Color.LIGHT_GRAY);
            
            row.add(btn);
            row.add(desc);
            itemPanel.add(row);
        }
        
        panel.add(itemPanel, BorderLayout.CENTER);
        
        itemPickStatus = new JLabel("Select Item for Character 1", SwingConstants.CENTER);
        itemPickStatus.setFont(new Font("Arial", Font.BOLD, 16));
        itemPickStatus.setForeground(Color.CYAN);
        itemPickStatus.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(itemPickStatus, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void pickItem(int index) 
    {
        if (pickedItem1 == -1) 
        {
            pickedItem1 = index;
            game.equipItem(0, index);
            
            itemPickStatus.setText("Select Item for Character 2");
        } 
        else if (pickedItem2 == -1 && index != pickedItem1) 
        {
            pickedItem2 = index;
            game.equipItem(1, index);
            
            beginBattle();
        }
    }
    
    private JPanel makeBattleScreen() 
    {
        JPanel panel = new JPanel(new BorderLayout());
        
        boolean bgLoaded = false;
        try 
        {
            ImageIcon bgIcon = new ImageIcon("background.png");
            if (bgIcon.getIconWidth() > 0) 
            {
                final Image bg = bgIcon.getImage();
                panel = new JPanel(new BorderLayout()) 
                {
                    @Override
                    protected void paintComponent(Graphics g) 
                    {
                        super.paintComponent(g);
                        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                    }
                };
                bgLoaded = true;
            }
        } 
        catch (Exception e) 
        {
            // I can put nothing in here???
        }
        
        if (!bgLoaded) 
        {
            panel.setBackground(new Color(34, 139, 34));
        }
        
        JPanel characterPanel = new JPanel(new BorderLayout());
        characterPanel.setOpaque(false);
        
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        JLabel playerChar = new JLabel();
        try 
        {
            ImageIcon icon = new ImageIcon("character_1.png");
            if (icon.getIconWidth() > 0) 
            {
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                playerChar.setIcon(new ImageIcon(img));
            }
        } 
        catch (Exception ex) 
        {
        }
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(playerChar);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 100, 0));
        
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        JLabel enemyChar = new JLabel();
        try 
        {
            ImageIcon icon = new ImageIcon("character_2.png");
            if (icon.getIconWidth() > 0) 
            {
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                enemyChar.setIcon(new ImageIcon(img));
            }
        } 
        catch (Exception ex) 
        {
        }
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(enemyChar);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 50));
        
        characterPanel.add(leftPanel, BorderLayout.WEST);
        characterPanel.add(rightPanel, BorderLayout.EAST);
        
        panel.add(characterPanel, BorderLayout.CENTER);
        
        JPanel hpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        hpPanel.setOpaque(false);
        
        goodGuyHpBars = new JProgressBar[2];
        badGuyHpBars = new JProgressBar[2];
        goodGuyLabels = new JLabel[2];
        badGuyLabels = new JLabel[2];
        
        JLabel charImg1 = new JLabel();
        JLabel charImg2 = new JLabel();
        try 
        {
            ImageIcon icon1 = new ImageIcon("character_1.png");
            ImageIcon icon2 = new ImageIcon("character_2.png");
            if (icon1.getIconWidth() > 0) 
            {
                Image i1 = icon1.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
                charImg1.setIcon(new ImageIcon(i1));
            }
            if (icon2.getIconWidth() > 0) 
            {
                Image i2 = icon2.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
                charImg2.setIcon(new ImageIcon(i2));
            }
        } 
        catch (Exception ex) 
        {
        }
        
        hpPanel.add(charImg1);
        
        JLabel yourTeam = new JLabel("YOUR TEAM:");
        yourTeam.setForeground(Color.WHITE);
        yourTeam.setFont(new Font("Arial", Font.BOLD, 14));
        hpPanel.add(yourTeam);
        
        for (int i = 0; i < 2; i++) 
        {
            goodGuyHpBars[i] = makeHpBar();
            goodGuyLabels[i] = new JLabel();
            goodGuyLabels[i].setForeground(Color.WHITE);
            hpPanel.add(goodGuyLabels[i]);
            hpPanel.add(goodGuyHpBars[i]);
        }
        
        JLabel enemyTeam = new JLabel("ENEMIES:");
        enemyTeam.setForeground(Color.WHITE);
        enemyTeam.setFont(new Font("Arial", Font.BOLD, 14));
        hpPanel.add(enemyTeam);
        
        for (int i = 0; i < 2; i++) 
        {
            badGuyHpBars[i] = makeHpBar();
            badGuyLabels[i] = new JLabel();
            badGuyLabels[i].setForeground(Color.WHITE);
            hpPanel.add(badGuyLabels[i]);
            hpPanel.add(badGuyHpBars[i]);
        }
        
        hpPanel.add(charImg2);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        
        button = new JButton[8];
        String[] btnLabels = {"Attack Enemy 1", "Attack Enemy 2", "Use Ultimate", 
                              "Use Item", "Wait", "Help"};
        
        for (int i = 0; i < 6; i++) 
        {
            final int index = i;
            button[i] = new JButton(btnLabels[i]);
            button[i].setFont(new Font("Arial", Font.BOLD, 12));
            button[i].addActionListener(e -> doAction(index));
            buttonPanel.add(button[i]);
        }
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(hpPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JProgressBar makeHpBar() 
    {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(100);
        bar.setStringPainted(true);
        bar.setPreferredSize(new Dimension(150, 25));
        bar.setForeground(Color.GREEN);
        return bar;
    }
    
    private void beginBattle() 
    {
        refreshDisplay();
        cardLayout.show(mainPanel, "BATTLE");
    }
    
    private void doAction(int action) 
    {
        int playerIdx = 0;
        
        if (!game.getPlayerTeam()[0].isAlive && game.getPlayerTeam()[1].isAlive) 
        {
            playerIdx = 1;
        }
        
        switch (action) 
        {
            case 0:
                game.playerAttack(playerIdx, 0);
                break;
            case 1:
                game.playerAttack(playerIdx, 1);
                break;
            case 2:
                game.playerUltimate(playerIdx);
                break;
            case 3:
                game.playerUseItem(playerIdx);
                break;
            case 4:
                break;
            case 5:
                showHelpInfo();
                return;
        }
        
        refreshDisplay();
        
        if (game.isGameOver()) 
        {
            triggerEndScreen();
        }
    }
    
    private void refreshDisplay() 
    {
        Character[] players = game.getPlayerTeam();
        Character[] enemies = game.getEnemyTeam();
        
        for (int i = 0; i < 2; i++) 
        {
            updateBarThing(goodGuyHpBars[i], players[i]);
            goodGuyLabels[i].setText(players[i].name.substring(4));
            
            updateBarThing(badGuyHpBars[i], enemies[i]);
            badGuyLabels[i].setText(enemies[i].name.substring(4));
        }
    }
    
    private void updateBarThing(JProgressBar bar, Character c) 
    {
        bar.setMaximum(c.maxHp);
        bar.setValue(c.currentHp);
        bar.setString(c.currentHp + " / " + c.maxHp + " HP");
        
        if (c.currentHp > c.maxHp * 0.5) 
        {
            bar.setForeground(Color.GREEN);
        } 
        else if (c.currentHp > c.maxHp * 0.25) 
        {
            bar.setForeground(Color.YELLOW);
        } 
        else 
        {
            bar.setForeground(Color.RED);
        }
        
        if (!c.isAlive) 
        {
            bar.setForeground(Color.DARK_GRAY);
            bar.setString("DEFEATED");
        }
    }
    
    private void showHelpInfo() 
    {
        String help = "=== HELP ===\n" +
                     "Knight: Counter reflects 2.5x damage\n" +
                     "Robot: Rocket hits all enemies for 50\n" +
                     "Witch: Revives allies or heals 20 HP\n\n" +
                     "Items consume after use (except Boots)\n" +
                     "Ultimate charges each turn\n";
        JOptionPane.showMessageDialog(this, help, "Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private JPanel makeEndScreen() 
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.BLACK);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(20, 20, 20, 20);
        
        endGameLabel = new JLabel("GAME OVER");
        endGameLabel.setFont(new Font("Arial", Font.BOLD, 48));
        endGameLabel.setForeground(Color.RED);
        c.gridy = 0;
        panel.add(endGameLabel, c);
        
        JButton playAgain = new JButton("Play Again");
        playAgain.setFont(new Font("Arial", Font.BOLD, 24));
        playAgain.setPreferredSize(new Dimension(200, 50));
        playAgain.addActionListener(e -> 
        {
            game = new GameBackend();
            pickedChar1 = -1;
            pickedChar2 = -1;
            pickedItem1 = -1;
            pickedItem2 = -1;
            cardLayout.show(mainPanel, "START");
        });
        c.gridy = 1;
        panel.add(playAgain, c);
        
        JButton quit = new JButton("Quit");
        quit.setFont(new Font("Arial", Font.BOLD, 24));
        quit.setPreferredSize(new Dimension(200, 50));
        quit.addActionListener(e -> System.exit(0));
        c.gridy = 2;
        panel.add(quit, c);
        
        return panel;
    }
    
    private void triggerEndScreen() 
    {
        if (game.didPlayerWin()) 
        {
            endGameLabel.setText("VICTORY!");
            endGameLabel.setForeground(Color.GREEN);
        } 
        else 
        {
            endGameLabel.setText("DEFEAT");
            endGameLabel.setForeground(Color.RED);
        }
        
        cardLayout.show(mainPanel, "GAMEOVER");
    }
    
    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(() -> new GameFrontend());
    }
}

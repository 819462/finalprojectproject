import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class GameFrontend extends JFrame {
    
    // Game backend
    private GameBackend game;
    
    // UI Components
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Character selection
    private int selectedChar1 = -1;
    private int selectedChar2 = -1;
    private String[] charNames = {"Knight", "Robot", "Witch"};
    private String[] charDescs = {
        "HP:250 Speed:2 Atk:30 Ult:Counter",
        "HP:300 Speed:1 Atk:35 Ult:Rocket", 
        "HP:200 Speed:3 Atk:20 Ult:Revive"
    };
    
    // Item selection
    private int selectedItem1 = -1;
    private int selectedItem2 = -1;
    private String[] itemNames = {"Shield", "Potion", "Knife", "Boots", "Blow Dart"};
    private String[] itemDescs = {
        "Blocks all damage once",
        "Heals 40 HP",
        "+50% damage for 2 turns",
        "+2 Speed permanently",
        "Poison enemy (3 uses)"
    };
    
    // Battle components
    private JProgressBar[] playerHpBars;
    private JProgressBar[] enemyHpBars;
    private JLabel[] playerLabels;
    private JLabel[] enemyLabels;
    private JButton[] actionButtons;
    
    // Status labels for selection screens
    private JLabel charSelectStatus;
    private JLabel itemSelectStatus;
    private JLabel gameOverLabel;
    
    public GameFrontend() {
        super("Combat Game - Without MSG Guaranteed");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        
        // Debug: Print working directory
        DebugEngine.log("Working directory: " + System.getProperty("user.dir"));
        DebugEngine.log("Looking for images in: " + System.getProperty("user.dir"));
        
        game = new GameBackend();
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create all screens
        mainPanel.add(createStartScreen(), "START");
        mainPanel.add(createCharSelectScreen(), "CHAR_SELECT");
        mainPanel.add(createItemSelectScreen(), "ITEM_SELECT");
        mainPanel.add(createBattleScreen(), "BATTLE");
        mainPanel.add(createGameOverScreen(), "GAMEOVER");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "START");
        
        setVisible(true);
    }
    
    // ========== START SCREEN ==========
    private JPanel createStartScreen() {
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
        playButton.addActionListener(e -> {
            selectedChar1 = -1;
            selectedChar2 = -1;
            cardLayout.show(mainPanel, "CHAR_SELECT");
        });
        c.gridy = 2;
        panel.add(playButton, c);
        
        return panel;
    }
    
    // ========== CHARACTER SELECT SCREEN ==========
    private JPanel createCharSelectScreen() {
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
        
        for (int i = 0; i < 3; i++) {
            final int charIndex = i;
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            row.setBackground(new Color(50, 50, 50));
            
            JButton btn = new JButton(charNames[i]);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.setPreferredSize(new Dimension(120, 50));
            btn.addActionListener(e -> selectCharacter(charIndex));
            
            JLabel desc = new JLabel(charDescs[i]);
            desc.setFont(new Font("Arial", Font.PLAIN, 14));
            desc.setForeground(Color.LIGHT_GRAY);
            
            row.add(btn);
            row.add(desc);
            charPanel.add(row);
        }
        
        panel.add(charPanel, BorderLayout.CENTER);
        
        charSelectStatus = new JLabel("Select Character 1", SwingConstants.CENTER);
        charSelectStatus.setFont(new Font("Arial", Font.BOLD, 16));
        charSelectStatus.setForeground(Color.YELLOW);
        charSelectStatus.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(charSelectStatus, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void selectCharacter(int index) {
        if (selectedChar1 == -1) {
            selectedChar1 = index;
            DebugEngine.log("Selected Char 1: " + charNames[index]);
            
            // Update status label
            charSelectStatus.setText("Select Character 2 (different from " + charNames[index] + ")");
        } else if (selectedChar2 == -1 && index != selectedChar1) {
            selectedChar2 = index;
            DebugEngine.log("Selected Char 2: " + charNames[index]);
            
            // Create teams and move to item selection
            game.createTeams(selectedChar1, selectedChar2);
            cardLayout.show(mainPanel, "ITEM_SELECT");
        }
    }
    
    // ========== ITEM SELECT SCREEN ==========
    private JPanel createItemSelectScreen() {
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
        
        for (int i = 0; i < 5; i++) {
            final int itemIndex = i;
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            row.setBackground(new Color(50, 50, 50));
            
            JButton btn = new JButton(itemNames[i]);
            btn.setFont(new Font("Arial", Font.BOLD, 16));
            btn.setPreferredSize(new Dimension(120, 40));
            btn.addActionListener(e -> selectItem(itemIndex));
            
            JLabel desc = new JLabel(itemDescs[i]);
            desc.setFont(new Font("Arial", Font.PLAIN, 14));
            desc.setForeground(Color.LIGHT_GRAY);
            
            row.add(btn);
            row.add(desc);
            itemPanel.add(row);
        }
        
        panel.add(itemPanel, BorderLayout.CENTER);
        
        itemSelectStatus = new JLabel("Select Item for Character 1", SwingConstants.CENTER);
        itemSelectStatus.setFont(new Font("Arial", Font.BOLD, 16));
        itemSelectStatus.setForeground(Color.CYAN);
        itemSelectStatus.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.add(itemSelectStatus, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void selectItem(int index) {
        if (selectedItem1 == -1) {
            selectedItem1 = index;
            game.equipItem(0, index);
            DebugEngine.log("Equipped Item 1: " + itemNames[index]);
            
            // Update status
            itemSelectStatus.setText("Select Item for Character 2");
        } else if (selectedItem2 == -1 && index != selectedItem1) {
            selectedItem2 = index;
            game.equipItem(1, index);
            DebugEngine.log("Equipped Item 2: " + itemNames[index]);
            
            // Start battle
            startBattle();
        }
    }
    
    // ========== BATTLE SCREEN ==========
    private JPanel createBattleScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Background - try to load, use nice green if not found
        boolean bgLoaded = false;
        try {
            ImageIcon bgIcon = new ImageIcon("background.png");
            if (bgIcon.getIconWidth() > 0) {
                final Image bg = bgIcon.getImage();
                DebugEngine.log("Background image loaded successfully!");
                panel = new JPanel(new BorderLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                    }
                };
                bgLoaded = true;
            }
        } catch (Exception e) {
            DebugEngine.error("Failed to load background: " + e.getMessage());
        }
        
        if (!bgLoaded) {
            DebugEngine.warn("Background image not found! Using forest green fallback.");
            panel.setBackground(new Color(34, 139, 34)); // Forest green
        }
        
        // Character sprites on battlefield
        JPanel characterPanel = new JPanel(new BorderLayout());
        characterPanel.setOpaque(false);
        
        // Player character on the LEFT
        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        JLabel playerChar = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("character_1.png");
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                playerChar.setIcon(new ImageIcon(img));
                DebugEngine.log("Player character sprite loaded!");
            }
        } catch (Exception ex) {
            DebugEngine.error("Failed to load player character: " + ex.getMessage());
        }
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(playerChar);
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 100, 0));
        
        // Enemy character on the RIGHT
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        JLabel enemyChar = new JLabel();
        try {
            ImageIcon icon = new ImageIcon("character_2.png");
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                enemyChar.setIcon(new ImageIcon(img));
                DebugEngine.log("Enemy character sprite loaded!");
            }
        } catch (Exception ex) {
            DebugEngine.error("Failed to load enemy character: " + ex.getMessage());
        }
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(enemyChar);
        rightPanel.add(Box.createVerticalGlue());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 100, 50));
        
        characterPanel.add(leftPanel, BorderLayout.WEST);
        characterPanel.add(rightPanel, BorderLayout.EAST);
        
        panel.add(characterPanel, BorderLayout.CENTER);
        
        // HP Panel
        JPanel hpPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        hpPanel.setOpaque(false);
        
        playerHpBars = new JProgressBar[2];
        enemyHpBars = new JProgressBar[2];
        playerLabels = new JLabel[2];
        enemyLabels = new JLabel[2];
        
        // Character images
        JLabel charImg1 = new JLabel();
        JLabel charImg2 = new JLabel();
        try {
            ImageIcon icon1 = new ImageIcon("character_1.png");
            ImageIcon icon2 = new ImageIcon("character_2.png");
            if (icon1.getIconWidth() > 0) {
                Image i1 = icon1.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
                charImg1.setIcon(new ImageIcon(i1));
                DebugEngine.log("Character 1 image loaded!");
            }
            if (icon2.getIconWidth() > 0) {
                Image i2 = icon2.getImage().getScaledInstance(56, 56, Image.SCALE_SMOOTH);
                charImg2.setIcon(new ImageIcon(i2));
                DebugEngine.log("Character 2 image loaded!");
            }
        } catch (Exception ex) {
            DebugEngine.error("Failed to load character images: " + ex.getMessage());
        }
        
        // Add character 1 image
        hpPanel.add(charImg1);
        
        JLabel yourTeam = new JLabel("YOUR TEAM:");
        yourTeam.setForeground(Color.WHITE);
        yourTeam.setFont(new Font("Arial", Font.BOLD, 14));
        hpPanel.add(yourTeam);
        
        for (int i = 0; i < 2; i++) {
            playerHpBars[i] = createHpBar();
            playerLabels[i] = new JLabel();
            playerLabels[i].setForeground(Color.WHITE);
            hpPanel.add(playerLabels[i]);
            hpPanel.add(playerHpBars[i]);
        }
        
        JLabel enemyTeam = new JLabel("ENEMIES:");
        enemyTeam.setForeground(Color.WHITE);
        enemyTeam.setFont(new Font("Arial", Font.BOLD, 14));
        hpPanel.add(enemyTeam);
        
        for (int i = 0; i < 2; i++) {
            enemyHpBars[i] = createHpBar();
            enemyLabels[i] = new JLabel();
            enemyLabels[i].setForeground(Color.WHITE);
            hpPanel.add(enemyLabels[i]);
            hpPanel.add(enemyHpBars[i]);
        }
        
        // Add character 2 image at the end
        hpPanel.add(charImg2);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setOpaque(false);
        
        actionButtons = new JButton[8];
        String[] btnLabels = {"Attack Enemy 1", "Attack Enemy 2", "Use Ultimate", 
                              "Use Item", "Wait", "Help"};
        
        for (int i = 0; i < 6; i++) {
            final int index = i;
            actionButtons[i] = new JButton(btnLabels[i]);
            actionButtons[i].setFont(new Font("Arial", Font.BOLD, 12));
            actionButtons[i].addActionListener(e -> handleAction(index));
            buttonPanel.add(actionButtons[i]);
        }
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(hpPanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JProgressBar createHpBar() {
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(100);
        bar.setStringPainted(true);
        bar.setPreferredSize(new Dimension(150, 25));
        bar.setForeground(Color.GREEN);
        return bar;
    }
    
    private void startBattle() {
        updateBattleDisplay();
        DebugEngine.log("Battle started!");
        cardLayout.show(mainPanel, "BATTLE");
    }
    
    private void handleAction(int action) {
        DebugEngine.log("Action: " + action);
        
        // Determine which player is acting (simplify: always player 0 for now)
        int playerIndex = 0;
        
        // Find alive player
        if (!game.getPlayerTeam()[0].isAlive && game.getPlayerTeam()[1].isAlive) {
            playerIndex = 1;
        }
        
        switch (action) {
            case 0: // Attack enemy 1
                game.playerAttack(playerIndex, 0);
                break;
            case 1: // Attack enemy 2
                game.playerAttack(playerIndex, 1);
                break;
            case 2: // Ultimate
                game.playerUltimate(playerIndex);
                break;
            case 3: // Item
                game.playerUseItem(playerIndex);
                break;
            case 4: // Wait
                DebugEngine.log("Player waits...");
                break;
            case 5: // Help
                showHelp();
                return;
        }
        
        updateBattleDisplay();
        
        // Log to console instead
        DebugEngine.log(game.getBattleLog());
        
        if (game.isGameOver()) {
            showGameOver();
        }
    }
    
    private void updateBattleDisplay() {
        GameBackend.Character[] players = game.getPlayerTeam();
        GameBackend.Character[] enemies = game.getEnemyTeam();
        
        for (int i = 0; i < 2; i++) {
            updateHpBar(playerHpBars[i], players[i]);
            playerLabels[i].setText(players[i].name.substring(4)); // Remove "O's "
            
            updateHpBar(enemyHpBars[i], enemies[i]);
            enemyLabels[i].setText(enemies[i].name.substring(4)); // Remove "X's "
        }
        
        DebugEngine.printGameState(game);
    }
    
    private void updateHpBar(JProgressBar bar, GameBackend.Character c) {
        bar.setMaximum(c.maxHp);
        bar.setValue(c.currentHp);
        bar.setString(c.currentHp + " / " + c.maxHp + " HP");
        
        if (c.currentHp > c.maxHp * 0.5) {
            bar.setForeground(Color.GREEN);
        } else if (c.currentHp > c.maxHp * 0.25) {
            bar.setForeground(Color.YELLOW);
        } else {
            bar.setForeground(Color.RED);
        }
        
        if (!c.isAlive) {
            bar.setForeground(Color.DARK_GRAY);
            bar.setString("DEFEATED");
        }
    }
    
    private void showHelp() {
        String help = "=== HELP ===\n" +
                     "Knight: Counter reflects 2.5x damage\n" +
                     "Robot: Rocket hits all enemies for 50\n" +
                     "Witch: Revives allies or heals 20 HP\n\n" +
                     "Items consume after use (except Boots)\n" +
                     "Ultimate charges each turn\n";
        JOptionPane.showMessageDialog(this, help, "Help", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ========== GAME OVER SCREEN ==========
    private JPanel createGameOverScreen() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.BLACK);
        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(20, 20, 20, 20);
        
        gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 48));
        gameOverLabel.setForeground(Color.RED);
        c.gridy = 0;
        panel.add(gameOverLabel, c);
        
        JButton playAgain = new JButton("Play Again");
        playAgain.setFont(new Font("Arial", Font.BOLD, 24));
        playAgain.setPreferredSize(new Dimension(200, 50));
        playAgain.addActionListener(e -> {
            game = new GameBackend();
            selectedChar1 = -1;
            selectedChar2 = -1;
            selectedItem1 = -1;
            selectedItem2 = -1;
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
    
    private void showGameOver() {
        // Update game over label
        if (game.didPlayerWin()) {
            gameOverLabel.setText("VICTORY!");
            gameOverLabel.setForeground(Color.GREEN);
        } else {
            gameOverLabel.setText("DEFEAT");
            gameOverLabel.setForeground(Color.RED);
        }
        
        cardLayout.show(mainPanel, "GAMEOVER");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrontend());
    }
}

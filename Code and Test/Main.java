
public class Main {
    public static boolean isPlaying = false;

    
    private static Player player;

    public static void main(String[] args) throws InterruptedException {
        
        player = new Player(false);

       
        player.startPlaying(new Player.TimeCheckCallback() {
            @Override
            public void onTimeRestrictionViolation(String allowedTimeRange) {
        
            }

            @Override
            public void onPeriodicCheck(boolean isAllowed) {
        
            }
        });

       
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (player != null) {
                player.stopPlaying();
            }
        }));

        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        mainMenuScreen.setVisible(true);
        MusicPlayer.getInstance().play("resources/music1.wav");
        MusicPlayer.getInstance().setVolume(90);
    }

  
    public static Player getPlayer() {
        return player;
    }
}
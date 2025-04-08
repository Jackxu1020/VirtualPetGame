public enum PetType {

    DUCK(100, 100, 100, 100, 1,
            1,2),

    SHEEP(100, 100, 100, 100, 2 ,
            1,1),

    DOG(100, 100, 100, 100, 1,
            2,1);


    private final int defaultMaxHealth;
    private final int defaultMaxFullness;
    private final int defaultMaxHappiness;
    private final int defaultMaxSleep;


    private final int hungerDecayRate;
    private final int happinessDecayRate;
    private final int sleepDecayRate;

    PetType(int defaultMaxHealth,
            int defaultMaxFullness,
            int defaultMaxHappiness,
            int defaultMaxSleep,
            int hungerDecayRate,
            int happinessDecayRate,
            int sleepDecayRate) {
        this.defaultMaxHealth = defaultMaxHealth;
        this.defaultMaxFullness = defaultMaxFullness;
        this.defaultMaxHappiness = defaultMaxHappiness;
        this.defaultMaxSleep = defaultMaxSleep;
        this.hungerDecayRate = hungerDecayRate;
        this.happinessDecayRate = happinessDecayRate;
        this.sleepDecayRate = sleepDecayRate;
    }

    public int getDefaultMaxHealth() {
        return defaultMaxHealth;
    }

    public int getDefaultMaxFullness() {
        return defaultMaxFullness;
    }

    public int getDefaultMaxHappiness() {
        return defaultMaxHappiness;
    }

    public int getDefaultMaxSleep() {
        return defaultMaxSleep;
    }

    public int getHungerDecayRate() {
        return hungerDecayRate;
    }

    public int getHappinessDecayRate() {
        return happinessDecayRate;
    }

    public int getSleepDecayRate() {
        return sleepDecayRate;
    }
}
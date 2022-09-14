import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.*;
import java.math.*;

/*solution to go Bronze ligue.*/
class Player {

    static final int MAX_CARRIED_SAMPLES = 3;
    static final int MAX_CARRIED_MOLECULES = 10;
    static final int NB_PLAYERS = 2;
    static final String WAIT = "WAIT";
    static final String GOTO = "GOTO";
    static final String SPACE_SEPARATOR = " ";
    static final String DIAG = "DIAGNOSIS";
    static final String MOL = "MOLECULES";
    static final String LAB = "LABORATORY";
    static final String CONNECT = "CONNECT ";
    static final EnumMap<PlayerType, Robot> robotMap = new EnumMap<>(PlayerType.class);

    // static final StateMachine stateMachine = StateMachine.init;
    static final EnumMap<MoleculeType, Integer> molAvailMap = new EnumMap<>(MoleculeType.class);
    static LinkedList<Sample> listSample = new LinkedList<>();

    public static void main(String args[]) {

        robotMap.put(PlayerType.Me, new Robot(PlayerType.Me));
        robotMap.put(PlayerType.Enemy, new Robot(PlayerType.Enemy));

        Scanner in = new Scanner(System.in);
        // mandatory for codingame purpose
        int projectCount = in.nextInt();

        // game loop
        while (true) {
            // step 1 general stuff
            for (int i = 0; i < NB_PLAYERS; i++) {
                Robot robot = robotMap.get(PlayerType.fromInt(i + 1));

                robot.position = Position.valueOf(in.next());
                robot.eta = in.nextInt();
                robot.score = in.nextInt();

                for (int j = 0; j < MoleculeType.MOlTYPE_INDEXED.length; j++) {
                    robot.myStorageMap.put(MoleculeType.MOlTYPE_INDEXED[j], in.nextInt());
                }
                for (int j = 0; j < MoleculeType.MOlTYPE_INDEXED.length; j++) {
                    robot.myExpertiseMap.put(MoleculeType.MOlTYPE_INDEXED[j], in.nextInt());
                }

                System.err.println(robot.toString());
            }

            // available molecules
            for (int j = 0; j < MoleculeType.MOlTYPE_INDEXED.length; j++) {
                molAvailMap.put(MoleculeType.MOlTYPE_INDEXED[j], in.nextInt());
            }
            System.err.println(molAvailMap.toString());

            // step 4 samples
            listSample.clear();
            int sampleCount = in.nextInt();
            System.err.println("sampleCount >" + sampleCount + "<");

            for (int i = 0; i < sampleCount; i++) {
                int sampleId = in.nextInt();
                CarriedBy carriedBy = CarriedBy.fromInt(in.nextInt());// méthode fromInt?
                int rank = in.nextInt();
                String expertiseGain = in.next();
                int health = in.nextInt();
                List<Integer> costList = new LinkedList<>();
                for (int j = 0; j < MoleculeType.MOlTYPE_INDEXED.length; j++) {
                    costList.add(in.nextInt());
                }
                Sample s = new Sample(sampleId, carriedBy, rank, expertiseGain, health, costList);

                /*
                 * if (sampleList.stream().map(a ->
                 * a.getSampleId()).collect(Collectors.toList()).contains(sampleId)) {
                 * System.err.println("Samples intéressant" + s);
                 * }
                 */
                listSample.add(s);
            }
            listSample.forEach(System.err::println);

            if (canProduce().isPresent()) {
                produceAction(canProduce().get());
            }
            // if not possible, gather molecules
            else if (getAMoleculeToGatheToCompletSample().isPresent()) {
                gatherAction(getAMoleculeToGatheToCompletSample().get());
            }
            // if not possible, diag sample
            else if (canDiagSample().isPresent()) {
                diagAction(canDiagSample().get());
            }
            // if not possible collect sample
            else if (canCollectSample()) {
                if (robotMap.get(PlayerType.Me).position.equals(Position.SAMPLES)) {
                    collect(SampleRank.Two);
                } else {
                    gotoPos(Position.SAMPLES);
                }
            } else {
                doNothing();
            }
        }
    }

    private static void doNothing() {
        System.out.println(WAIT);
    }

    private static void gotoPos(Position pos) {
        System.out.println(GOTO + SPACE_SEPARATOR + pos.getText());
    }

    /* collect a sample */
    private static void collect(SampleRank rank) {
        System.out.println(CONNECT + SPACE_SEPARATOR + rank.getText());
    }

    /* analyse a sample */
    private static void diagnostic(Sample sample) {
        System.out.println(CONNECT + SPACE_SEPARATOR + sample.getId());
    }

    /* download a composition of sample */
    private static void download(Sample sample) {
        System.out.println(CONNECT + SPACE_SEPARATOR + sample.getId());
    }

    /* get the molecule */
    private static void gather(MoleculeType mol) {
        System.out.println(CONNECT + SPACE_SEPARATOR + mol.getText());
    }

    /* produce a med */
    private static void produce(Sample sample) {
        System.out.println(CONNECT + SPACE_SEPARATOR + sample.getId());
    }

    private static void produceAction(Sample sample) {
        if (robotMap.get(PlayerType.Me).position.equals(Position.LABORATORY)) {
            produce(sample);
        } else {
            gotoPos(Position.LABORATORY);
        }
    }

    private static void gatherAction(MoleculeType mol) {
        if (robotMap.get(PlayerType.Me).position.equals(Position.MOLECULES)) {
            gather(mol);
        } else {
            gotoPos(Position.MOLECULES);
        }
    }

    private static void diagAction(Sample sample) {
        if (robotMap.get(PlayerType.Me).position.equals(Position.DIAGNOSIS)) {
            System.err.println("diagC " + sample.toString());
            diagnostic(sample);
        } else {
            gotoPos(Position.DIAGNOSIS);
        }
    }

    private static Consumer<Position> gotoC = (pos) -> System.out.println(GOTO + SPACE_SEPARATOR + pos.getText());
    private static Consumer<Sample> produceC = (sample) -> produceAction(sample);
    private static Consumer<MoleculeType> gatherC = (mol) -> gatherAction(mol);
    private static Consumer<Sample> diagC = (sample) -> diagAction(sample);

    private static Predicate<Sample> NotDiagnosised = s -> s.getCostMap().get(MoleculeType.A)!= -1;
    private static Predicate<Sample> CarryedByMeAndDiagnosisedF = s -> s.getCarriedBy() == CarriedBy.Me && s.getCostMap().get(MoleculeType.A)!= -1;
    private static Predicate<Sample> CarryedByMeAndNotDiagnosisedF = s -> s.getCarriedBy() == CarriedBy.Me && s.getCostMap().get(MoleculeType.A)== -1;
    
    // if not possible, diag sample already
    private static Optional<Sample> canDiagSample() {
        System.err.println("canDiagSample "
                + listSample.stream().filter(s -> s.getCarriedBy() == CarriedBy.Me).findFirst().toString());
        return listSample.stream().filter(CarryedByMeAndNotDiagnosisedF).findFirst();
    }

    // if not possible collect sample
    private static boolean canCollectSample() {
        System.err.println("canCollectSample "
                + (listSample.stream().filter(s -> s.getCarriedBy() == CarriedBy.Me).count() < MAX_CARRIED_SAMPLES));
        return listSample.stream().filter(s -> s.getCarriedBy() == CarriedBy.Me).count() < MAX_CARRIED_SAMPLES;
        // return canCollectSample(robotMap.get(PlayerType.Me));
    }

    private static Optional<Sample> canProduce() {
        return canProduce(robotMap.get(PlayerType.Me));
    }

    private static Optional<Sample> canProduce(Robot robot) {
        for (Sample sample : listSample.stream().filter(s -> s.getCarriedBy() == CarriedBy.Me).collect(Collectors.toList())){
        //for (Sample sample : robot.sampleList) {
            
            if (canProduceSample(sample, robot))
                return Optional.of(sample);
        }
        return Optional.empty();
    }

    private static boolean canProduceSample(Sample sample, Robot robot) {
        if(robot.myStorageMap.values().stream().mapToInt(Integer::intValue).sum()  < sample.costMap.values().stream().mapToInt(Integer::intValue).sum()){
            return false;
        }
        
        for (int j = 0; j < MoleculeType.MOlTYPE_INDEXED.length; j++) {

            System.err.println("canProduceSample robot.myStorageMap.get(MoleculeType.MOlTYPE_INDEXED[j]) "+robot.myStorageMap.get(MoleculeType.MOlTYPE_INDEXED[j]));
            System.err.println("canProduceSample map "+sample.costMap.get(MoleculeType.MOlTYPE_INDEXED[j]));

            if (robot.myStorageMap.get(MoleculeType.MOlTYPE_INDEXED[j]) > sample.costMap
                    .get(MoleculeType.MOlTYPE_INDEXED[j])) {
                return false;
            }
        }
        return true;
    }

    private static Optional<MoleculeType> getAMoleculeToGatheToCompletSample() {
        return getAMoleculeToGatheToCompletSample(robotMap.get(PlayerType.Me));
    }

    
    private static Optional<MoleculeType> getAMoleculeToGatheToCompletSample(Robot robot) {
        /*
         * robot.sampleList.forEach(sample ->
         * getAMoleculeToGatheToCompletSample(sample).map(mol -> {
         * return Optional.of(mol);
         * }
         * ));
         */
        System.err.println("Some of my molecules "+robot.myStorageMap.values().stream().mapToInt(Integer::intValue).sum());        
        if (robot.myStorageMap.values().stream().mapToInt(Integer::intValue).sum() < MAX_CARRIED_MOLECULES) {
            //listSample.stream().filter(s -> s.getCarriedBy() == CarriedBy.Me).collect(Collectors.toList());
            // for (Sample sample : robot.sampleList) {
            for (Sample sample : listSample.stream().filter(CarryedByMeAndDiagnosisedF).collect(Collectors.toList())) {     
                Optional<MoleculeType> mol = getAMoleculeToGatheToCompletSample(sample);
                System.err.println("mol present"+mol);
                if (mol.isPresent())
                    return mol;
            }
        } else {
            return Optional.empty();
        }
        return Optional.empty();
    }

    private static Optional<MoleculeType> getAMoleculeToGatheToCompletSample(Sample sample) {
        return getAMoleculeToGatheToCompletSample(sample, robotMap.get(PlayerType.Me));
    }

    private static Optional<MoleculeType> getAMoleculeToGatheToCompletSample(Sample sample, Robot robot) {

        System.err.println(" getAMoleculeToGatheToCompletSample " + sample.toString());
        for (int j = 0; j < MoleculeType.MOlTYPE_INDEXED.length; j++) {
            // if this robot does have enough of mol type to produce the sample
            System.err.println("robot.myStorageMap.get(MoleculeType.MOlTYPE_INDEXED[j]) "+robot.myStorageMap.get(MoleculeType.MOlTYPE_INDEXED[j]));
            System.err.println("map "+sample.costMap.get(MoleculeType.MOlTYPE_INDEXED[j]));
            if (robot.myStorageMap.get(MoleculeType.MOlTYPE_INDEXED[j]) < sample.costMap
                    .get(MoleculeType.MOlTYPE_INDEXED[j])) {
                return Optional.of(MoleculeType.MOlTYPE_INDEXED[j]);
            }
        }
        return Optional.empty();
    }
}

/* domain class for robot. */
class Robot {
    /*all in public for easy access */
    public PlayerType playerType;
    public Position position = Position.START_POS;
    public int eta = 0;
    public int score = 0;
    public int storageA = 0;
    // public int nbMol = 0;
    public LinkedList<Sample> sampleList = new LinkedList<>();

    public EnumMap<MoleculeType, Integer> myStorageMap = new EnumMap<>(MoleculeType.class);
    public EnumMap<MoleculeType, Integer> myExpertiseMap = new EnumMap<>(MoleculeType.class);

    public Robot(PlayerType playerTypeParam) {
        this.playerType = playerTypeParam;
    }
    @Override
    public String toString() {
        return "Robot [eta=" + eta + ", myExpertiseMap=" + myExpertiseMap + ", myStorageMap=" + myStorageMap
                + ", playerType=" + playerType + ", score=" + score + ", storageA=" + storageA + ", position="
                + position
                + "]";
    }
}

/* Domain class for sample */
class Sample {
    private int id = 0;
    private CarriedBy carriedBy;
    private int rank = 0;
    private String expertiseGain = "";
    private int health = 0;

    /* basic numbers. */
    public EnumMap<MoleculeType, Integer> costMap = new EnumMap<>(MoleculeType.class);
    /* taking into acocunt the expertise */
    private Map<Character, Integer> realCostMap = Stream.of(new Object[][] {
            { 'A', 0 }, { 'B', 0 }, { 'C', 0 }, { 'D', 0 }, { 'E', 0 } })
            .collect(Collectors.toMap(data -> (Character) data[0], data -> (Integer) data[1]));

    public Map<Character, Integer> getRealCostMap() {
        return realCostMap;
    }

    private Integer total = 0;
    private Integer nbMol = 0;
    public Integer totalReal = 0;
    public Integer nbMolReal = 0;

    public Sample(int sampleIdP, CarriedBy carriedByP, int rankP, String expertiseP, int healthP,
            List<Integer> costList) {
        this.id = sampleIdP;
        carriedBy = carriedByP;
        rank = rankP;
        expertiseGain = expertiseP;
        health = healthP;
        for (int j = 0; j < MoleculeType.MOlTYPE_INDEXED.length; j++) {
            costMap.put(MoleculeType.MOlTYPE_INDEXED[j], costList.get(j));
        }

        for (Integer val : costMap.values()) {
            total += val;
        }

        if (costMap.get(MoleculeType.A) != 0)
            nbMol++;
        if (costMap.get(MoleculeType.B) != 0)
            nbMol++;
        if (costMap.get(MoleculeType.C) != 0)
            nbMol++;
        if (costMap.get(MoleculeType.D) != 0)
            nbMol++;
        if (costMap.get(MoleculeType.E) != 0)
            nbMol++;
    }

    public int getId() {
        return id;
    }

    public void setId(int sampleId) {
        this.id = sampleId;
    }

    public CarriedBy getCarriedBy() {
        return carriedBy;
    }

    public void setCarriedBy(CarriedBy carriedBy) {
        this.carriedBy = carriedBy;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getExpertiseGain() {
        return expertiseGain;
    }

    public void setExpertiseGain(String expertiseGain) {
        this.expertiseGain = expertiseGain;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public EnumMap<MoleculeType, Integer> getCostMap() {
        return costMap;
    }

    public void setCostMap(EnumMap<MoleculeType, Integer> costMap) {
        this.costMap = costMap;
    }

    public void setRealCostMap(Map<Character, Integer> realCostMap) {
        this.realCostMap = realCostMap;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getNbMol() {
        return nbMol;
    }

    public void setNbMol(Integer nbMol) {
        this.nbMol = nbMol;
    }

    public Integer getTotalReal() {
        return totalReal;
    }

    public void setTotalReal(Integer totalReal) {
        this.totalReal = totalReal;
    }

    public Integer getNbMolReal() {
        return nbMolReal;
    }

    public void setNbMolReal(Integer nbMolReal) {
        this.nbMolReal = nbMolReal;
    }

    @Override
    public String toString() {
        return "Sample [carriedBy=" + carriedBy + ", costMap=" + costMap + ", expertiseGain=" + expertiseGain
                + ", health=" + health + ", nbMol=" + nbMol + ", nbMolReal=" + nbMolReal + ", rank=" + rank
                + ", realCostMap=" + realCostMap + ", sampleId=" + id + ", total=" + total + ", totalReal="
                + totalReal + "]";
    }

}

/* Domain enum to know who carry the sample */
enum CarriedBy {
    Me(0),
    Enemy(1),
    Cloud(-1);

    private int value;

    CarriedBy(int val) {
        this.value = val;
    }

    public int getValue() {
        return this.value;
    }

    public static CarriedBy fromInt(int nb) {
        for (CarriedBy p : CarriedBy.values()) {
            if (p.value == nb) {
                return p;
            }
        }
        throw new IllegalArgumentException(String.valueOf(nb));
    }

}

/* rank of medecine. */
enum SampleRank {
    One("1"),
    Two("2"),
    Three("3");

    private String text;

    SampleRank(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}

/* Domain class for player. */
enum PlayerType {
    Me(1),
    Enemy(2);

    private int nb;

    PlayerType(int nbParam) {
        this.nb = nbParam;
    }

    public int getNb() {
        return this.nb;
    }

    /*
     * car le PlayerType.valueOf est sensible à la casse mais est ce que cela ne
     * fonctionne
     * qu'avec Me, Enemy ?
     */
    public static PlayerType fromInt(int nb) {
        for (PlayerType p : PlayerType.values()) {
            if (p.nb == nb) {
                return p;
            }
        }
        throw new IllegalArgumentException(String.valueOf(nb));
    }
}

enum Position {
    START_POS("START_POS"),
    SAMPLES("SAMPLES"),
    DIAGNOSIS("DIAGNOSIS"),
    MOLECULES("MOLECULES"),
    LABORATORY("LABORATORY");

    private String text;

    Position(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

}

enum MoleculeType {
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E");

    private String text;

    MoleculeType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    // avoir Months.values()[index] for performance
    public static MoleculeType[] MOlTYPE_INDEXED = new MoleculeType[] { A, B, C, D, E };

}

enum Expertise {
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E");

    private String text;

    Expertise(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    // avoir Months.values()[index] for performance
    public static Expertise[] EXPERTISE_INDEXED = new Expertise[] { A, B, C, D, E };

}

enum Storage {
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    E("E");

    private String text;

    Storage(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    // avoir Months.values()[index] for performance
    public static Storage[] STORAGE_INDEXED = new Storage[] { A, B, C, D, E };
}

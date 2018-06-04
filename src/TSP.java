import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import com.google.common.primitives.Ints;
import javax.swing.*;

public class TSP {
	
	private static int tournamentSize = 20;
	private static int DOUBLE_CROSSOVER = 0;

	private static final int cityShiftAmount = 60; //DO NOT CHANGE THIS.
	
    /**
     * How many cities to use.
     */
    protected static int cityCount;

    /**
     * How many chromosomes to use.
     */
    protected static int populationSize = 100; //DO NOT CHANGE THIS.

    /**
     * The part of the population eligable for mating.
     */
    protected static int matingPopulationSize;

    /**
     * The part of the population selected for mating.
     */
    protected static int selectedParents;

    /**
     * The current generation
     */
    protected static int generation;

    /**
     * The list of cities (with current movement applied).
     */
    protected static City[] cities;
    
    /**
     * The list of cities that will be used to determine movement.
     */
    private static City[] originalCities;

    /**
     * The list of chromosomes.
     */
    protected static Chromosome[] chromosomes;

    /**
    * Frame to display cities and paths
    */
    private static JFrame frame;

    /**
     * Integers used for statistical data
     */
    private static double min;
    private static double avg;
    private static double max;
    private static double sum;
    private static double genMin;

    /**
     * Width and Height of City Map, DO NOT CHANGE THESE VALUES!
     */
    private static int width = 600;
    private static int height = 600;


    private static Panel statsArea;
    private static TextArea statsText;


    /*
     * Writing to an output file with the costs.
     */
    private static void writeLog(String content) {
        String filename = "./output/results.out";
        FileWriter out;

        try {
            out = new FileWriter(filename, true);
            out.write(content + "\n");
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     *  Deals with printing same content to System.out and GUI
     */
    private static void print(boolean guiEnabled, String content) {
        if(guiEnabled) {
            statsText.append(content + "\n");
        }

        System.out.println(content);
    }
	
	
	/**
	 * Evolve given population to produce the next generation.
	 * @param population The population to evolve.
	 * @param cityList List of cities, needed for the Chromosome constructor calls you will be doing when mutating and breeding Chromosome instances
	 * @return The new generation of individuals.
	 */
   public static void evolve(){
//	  if (generation == 0) GenerateHeuristicRoutes();
      Chromosome [] parents = SelectParents(chromosomes, tournamentSize);
      Chromosome [] newPopulation = new Chromosome [populationSize];
      Chromosome parent1, parent2;
	  for (int i = 0; i < populationSize; i +=2){
		  parent1 = parents[i/2];
		  parent2 = parents[parents.length - i/2 - 1];
		  newPopulation[i] = CrossoverRight(parent1, parent2);
		  if (parent1.cost < parent2.cost)
			  newPopulation[i + 1] = CrossoverLeft(newPopulation[i], parent1); 
		  else
			  newPopulation[i + 1] = CrossoverLeft(newPopulation[i], parent2);
		  if(generation >= DOUBLE_CROSSOVER) {
			  parent1 = newPopulation[i];
			  newPopulation[i] = CrossoverRight(newPopulation[i], newPopulation[i + 1]);
			  if(parent1.cost < newPopulation[i + 1].cost)
				  newPopulation[i + 1] = CrossoverLeft(newPopulation[i], parent1);
			  else
				  newPopulation[i + 1] = CrossoverLeft(newPopulation[i], newPopulation[i + 1]);
		  }
		  
      }
      chromosomes = newPopulation;
   }
   
   public static void GenerateHeuristicRoutes() {
	   int [] randomStarts = GenerateRandomIndexes(populationSize, cityCount);
	   int currentIndex;
	   for (int i = 0; i < populationSize; ++i){   
		    Chromosome chromosome = chromosomes[i];
		    int [] cityList = chromosome.cityList;
		   	currentIndex = randomStarts[i];
		   	int [] newCityList = new int[cityCount];
		   	Arrays.fill(newCityList, -1);
		   	newCityList[0] = cityList[currentIndex];
		   	Random randomGenerator = new Random();
		   	int rightIndex = randomGenerator.nextInt(cityCount);
		   	int leftIndex = randomGenerator.nextInt(cityCount);
		   	for (int j = 1; j < cityCount; ++j ) {
		    	do {
		    		rightIndex = nextIndex(rightIndex, cityCount);
		    	} while (Ints.contains(newCityList, cityList[rightIndex]));
		    	
		    	do {
		    		leftIndex = previousIndex(leftIndex, cityCount);
		    	} while (Ints.contains(newCityList, cityList[leftIndex]));
		    	City currentCity = cities[cityList[currentIndex]];
		    	City rightCity = cities[cityList[rightIndex]];
		    	City leftCity = cities[cityList[leftIndex]];
		    	if (currentCity.proximity(rightCity) < currentCity.proximity(leftCity)) {
		    		newCityList[j] =  cityList[rightIndex];
		    		currentIndex = rightIndex;
		    	}
		    	else {
		    		newCityList[j] =  cityList[leftIndex];
		    		currentIndex = leftIndex;
		    	}	
		   	}
		   	
		   	chromosome.setCities(newCityList);
		   	chromosome.calculateCost(cities);
	   }
   }   
   
   
	public static Chromosome [] SelectParents(Chromosome [] population, int tournamentSize) {
		  int populationSize = population.length;
		  int newPopulationSize = populationSize/2;
	      Chromosome [] newPopulation = new Chromosome[newPopulationSize];
//	      int [] indexes = GenerateRandomIndexes(populationSize, populationSize);
	      Chromosome [] contestants = new Chromosome[tournamentSize];  
	      int [] indexes;
	      for (int i = 0; i < newPopulationSize; ++i){
	    	  indexes = GenerateRandomIndexes(tournamentSize, populationSize);
	    	  for(int j = 0; j < tournamentSize; ++j) {
	    		  contestants[j] = population[indexes[j]];
	    	  }
	    	  newPopulation[i] = Compete(contestants);
	      }  
	      return newPopulation;
		}
	
	private static int [] GenerateRandomIndexes(int number, int maxNum){
	    Random randomGenerator = new Random();
	    int [] values = new int[number];
	    for (int i = 0; i < number; ++i){
	      values[i] = randomGenerator.nextInt(maxNum);  
	    }
	    return values;
	}
	
	
	public static Chromosome Compete(Chromosome [] contestants){
		Chromosome.sortChromosomes(contestants, contestants.length);
		return contestants[0];
	}
	
	
	public static Chromosome CrossoverRight(Chromosome parent1, Chromosome parent2){
		int [] parent1CityIndexes = parent1.cityList;
		int [] parent2CityIndexes = parent2.cityList;
		int numberOfCities = parent1CityIndexes.length;
		int [] newCityList = new int[numberOfCities];
	//		used to check redundancy
		int [] usedCities = new int[numberOfCities];
		Arrays.fill(usedCities, -1);
		Random randomGenerator = new Random();
	    int currentIndex1 = randomGenerator.nextInt(numberOfCities); 
	    int city = parent1CityIndexes[currentIndex1];
	    int currentIndex2 = Ints.indexOf(parent2CityIndexes, city);
	// 	    Start with the randomly chosen city
	    newCityList[0] = city;
	    usedCities[0] = city;
	    for(int i = 1; i < numberOfCities; ++i) {
	    	int nextIndex1 = nextIndex(currentIndex1, numberOfCities);
	    	while (Ints.contains(usedCities, parent1CityIndexes[nextIndex1])) {
	    		nextIndex1 = nextIndex(nextIndex1, numberOfCities);
	    	} 	    	
	    	int nextIndex2 = nextIndex(currentIndex2, numberOfCities);
	    	while (Ints.contains(usedCities, parent2CityIndexes[nextIndex2])) {
	    		nextIndex2 = nextIndex(nextIndex2, numberOfCities);
	    	}
	    	
	    	if(parent1CityIndexes[nextIndex1] == parent2CityIndexes[nextIndex2]) {
	// 	    		common right node
//	    			System.out.println("common right");	
	 	    		city = parent1CityIndexes[nextIndex1];
	 	    		newCityList[i] = city;
	 	    		usedCities[i] = city;
	 	    		currentIndex1 = nextIndex1;
	 	    		currentIndex2 = nextIndex2;
	 	    		continue;
	 		   }	 
	 	    
// 	 	    Take closest right city
//	 	 	System.out.println("closest right");	
	 	    City contestant1;
	 	    City contestant2;	
	    	contestant1 = cities[parent1CityIndexes[nextIndex1]]; 
	    	contestant2 = cities[parent2CityIndexes[nextIndex2]]; 
	
	    	City currentCity = cities[newCityList[i - 1]];
	    	int distance1 = currentCity.proximity(contestant1);
	    	int distance2 = currentCity.proximity(contestant2);
	    	if(distance1 < distance2) {
	    		city = parent1CityIndexes[nextIndex1];
	    		newCityList[i] = city;
	    		usedCities[i] = city;
	    		currentIndex1 = nextIndex1;
	    		currentIndex2 = Ints.indexOf(parent2CityIndexes, currentIndex1);
	    	}
	    	else {
	    		city = parent2CityIndexes[nextIndex2];
	    		newCityList[i] = city;
	    		usedCities[i] = city;
	    		currentIndex2 = nextIndex2;
	    		currentIndex1 = Ints.indexOf(parent1CityIndexes, currentIndex2);    			
	    	}
	    }
 	    Chromosome offspring = new Chromosome(cities);
 	    offspring.setCities(newCityList);
 	    offspring.calculateCost(cities);
        return offspring;
	}
	
	
	public static Chromosome CrossoverLeft(Chromosome parent1, Chromosome parent2){
		int [] parent1CityIndexes = parent1.cityList;
		int [] parent2CityIndexes = parent2.cityList;
		int numberOfCities = parent1CityIndexes.length;
		int [] newCityList = new int[numberOfCities];
//		used to check redundancy
		int [] usedCities = new int[numberOfCities];
		Arrays.fill(usedCities, -1);
		Random randomGenerator = new Random();
 	    int currentIndex1 = randomGenerator.nextInt(numberOfCities); 
 	    int city = parent1CityIndexes[currentIndex1];
 	    int currentIndex2 = Ints.indexOf(parent2CityIndexes, city);
// 	    Start with the randomly chosen city
 	    newCityList[0] = city;
 	    usedCities[0] = city;
 	    for(int i = 1; i < numberOfCities; ++i) {
 	    	int previousIndex1 = previousIndex(currentIndex1, numberOfCities);
 	    	while (Ints.contains(usedCities, parent1CityIndexes[previousIndex1])) {
 	    		previousIndex1 = previousIndex(previousIndex1, numberOfCities);
 	    	}
 	    	int previousIndex2 = nextIndex(currentIndex2, numberOfCities);
 	    	while (Ints.contains(usedCities, parent2CityIndexes[previousIndex2])) {
 	    		previousIndex2 = nextIndex(previousIndex2, numberOfCities);
 	    	}
 	    	
 	 	    if(parent1CityIndexes[previousIndex1] == parent2CityIndexes[previousIndex2]) {
// 	 	    	Common left node
 	      		city = parent1CityIndexes[previousIndex1];
 	    		newCityList[i] = city;
 	    		usedCities[i] = city;
 	    		currentIndex1 = previousIndex1;
 	    		currentIndex2 = previousIndex2;
 	    		continue;
 		   	}
 	 	    
// 	 	    Take closest left city
 	 	    City contestant1;
 	 	    City contestant2;	
 	    	contestant1 = cities[parent1CityIndexes[previousIndex1]]; 
 	    	contestant2 = cities[parent2CityIndexes[previousIndex2]]; 

 	    	City currentCity = cities[newCityList[i - 1]];
 	    	int distance1 = currentCity.proximity(contestant1);
 	    	int distance2 = currentCity.proximity(contestant2);
 	    	if(distance1 < distance2) {
 	    		city = parent1CityIndexes[previousIndex1];
 	    		newCityList[i] = city;
 	    		usedCities[i] = city;
 	    		currentIndex1 = previousIndex1;
 	    		currentIndex2 = Ints.indexOf(parent2CityIndexes, currentIndex1);
 	    	}
 	    	else {
 	    		city = parent2CityIndexes[previousIndex2];
 	    		newCityList[i] = city;
 	    		usedCities[i] = city;
 	    		currentIndex2 = previousIndex2;
 	    		currentIndex1 = Ints.indexOf(parent1CityIndexes, currentIndex2);		
 	    	}
 	    }
 	    Chromosome offspring = new Chromosome(cities);
 	    offspring.setCities(newCityList);
 	    offspring.calculateCost(cities);
        return offspring;
	}
	
	/**
	 * The method used to generate a mutant of a chromosome
	 * @param original The chromosome to mutate.
	 * @param cityList list of cities, needed to instantiate the new Chromosome.
	 * @return Mutated chromosome.
	 */
//	public static Chromosome Mutate(Chromosome parent, City [] cities){
//      int [] cityList = parent.getCities();
//      Random randomGenerator = new Random();
////      Choose two cities randomly to swap their position
//      int city1Index = randomGenerator.nextInt(cityList.length);
//      int city2Index = randomGenerator.nextInt(cityList.length);;
//      int city1 = cityList[city1Index];
//      int city2 = cityList[city2Index];
//      parent.setCity(city1Index, city2);
//      parent.setCity(city2Index, city1);
//      parent.calculateCost(cities);
//      return parent;
//    }
	
	/**
	 * Breed two chromosomes to create a offspring
	 * @param parent1 First parent.
	 * @param parent2 Second parent.
	 * @param cityList list of cities, needed to instantiate the new Chromosome.
	 * @return Chromosome resuling from breeding parent.
	 */
//	public static Chromosome Breed(Chromosome parent1, Chromosome parent2, City [] cityList){
//      int [] cityIndexes = parent1.getCities();
//      City [] newCities = new City[cityIndexes.length];
//      
//      for (int i = 0; i<cityIndexes.length; ++i){
//         newCities[i] = cityList[cityIndexes[i]];
//      }
//      
//      return new Chromosome(newCities);
//   }	
	
	private static int nextIndex(int index, int numberOfCities) {
		return (index + 1) % numberOfCities;
	}
	
	private static int previousIndex(int index, int numberOfCities) {
		return (index - 1 + numberOfCities) % numberOfCities;
	}
	
//	private static boolean IsMutation() {
//	    Random randomGenerator = new Random();
//	    return randomGenerator.nextInt(100) < mutationProb ? true : false; 
//	}


    /**
     * Update the display
     */
    public static void updateGUI() {
        Image img = frame.createImage(width, height);
        Graphics g = img.getGraphics();
//        FontMetrics fm = g.getFontMetrics();

        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);

        if (true && (cities != null)) {
            for (int i = 0; i < cityCount; i++) {
                int xpos = cities[i].getx();
                int ypos = cities[i].gety();
                g.setColor(Color.green);
                g.fillOval(xpos - 5, ypos - 5, 10, 10);
                
                //// SHOW Outline of movement boundary
                // xpos = originalCities[i].getx();
                // ypos = originalCities[i].gety();
                // g.setColor(Color.darkGray);
                // g.drawLine(xpos + cityShiftAmount, ypos, xpos, ypos + cityShiftAmount);
                // g.drawLine(xpos, ypos + cityShiftAmount, xpos - cityShiftAmount, ypos);
                // g.drawLine(xpos - cityShiftAmount, ypos, xpos, ypos - cityShiftAmount);
                // g.drawLine(xpos, ypos - cityShiftAmount, xpos + cityShiftAmount, ypos);
            }

            g.setColor(Color.gray);
            for (int i = 0; i < cityCount; i++) {
                int icity = chromosomes[0].getCity(i);
                if (i != 0) {
                    int last = chromosomes[0].getCity(i - 1);
                    g.drawLine(
                        cities[icity].getx(),
                        cities[icity].gety(),
                        cities[last].getx(),
                        cities[last].gety());
                }
            }
                        
            int homeCity = chromosomes[0].getCity(0);
            int lastCity = chromosomes[0].getCity(cityCount - 1);
                        
            //Drawing line returning home
            g.drawLine(
                    cities[homeCity].getx(),
                    cities[homeCity].gety(),
                    cities[lastCity].getx(),
                    cities[lastCity].gety());
        }
        frame.getGraphics().drawImage(img, 0, 0, frame);
    }

    private static City[] LoadCitiesFromFile(String filename, City[] citiesArray) {
        ArrayList<City> cities = new ArrayList<City>();
        try 
        {
            FileReader inputFile = new FileReader(filename);
            BufferedReader bufferReader = new BufferedReader(inputFile);
            String line;
            while ((line = bufferReader.readLine()) != null) { 
                String [] coordinates = line.split(", ");
                cities.add(new City(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
            }

            bufferReader.close();

        } catch (Exception e) {
            System.out.println("Error while reading file line by line:" + e.getMessage());                      
        }
        
        citiesArray = new City[cities.size()];
        return cities.toArray(citiesArray);
    }

    private static City[] MoveCities(City[]cities) {
    	City[] newPositions = new City[cities.length];
        Random randomGenerator = new Random();

        for(int i = 0; i < cities.length; i++) {
        	int x = cities[i].getx();
        	int y = cities[i].gety();
        	
            int position = randomGenerator.nextInt(5);
            
            if(position == 1) {
            	y += cityShiftAmount;
            } else if(position == 2) {
            	x += cityShiftAmount;
            } else if(position == 3) {
            	y -= cityShiftAmount;
            } else if(position == 4) {
            	x -= cityShiftAmount;
            }
            
            newPositions[i] = new City(x, y);
        }
        
        return newPositions;
    }

    public static void main(String[] args) {
        DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String currentTime  = df.format(today);

        int runs;
        boolean display = false;
        String formatMessage = "Usage: java TSP 1 [gui] \n java TSP [Runs] [gui]";

        if (args.length < 1) {
            System.out.println("Please enter the arguments");
            System.out.println(formatMessage);
            display = false;
        } else {

            if (args.length > 1) {
                display = true; 
            }

            try {
                cityCount = 50;
                populationSize = 100;
                runs = Integer.parseInt(args[0]);

                if(display) {
                    frame = new JFrame("Traveling Salesman");
                    statsArea = new Panel();

                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.pack();
                    frame.setSize(width + 300, height);
                    frame.setResizable(false);
                    frame.setLayout(new BorderLayout());
                    
                    statsText = new TextArea(35, 35);
                    statsText.setEditable(false);

                    statsArea.add(statsText);
                    frame.add(statsArea, BorderLayout.EAST);
                    
                    frame.setVisible(true);
                }


                min = 0;
                avg = 0;
                max = 0;
                sum = 0;

                originalCities = cities = LoadCitiesFromFile("./data/CityList.txt", cities);

                writeLog("Run Stats for experiment at: " + currentTime);
                for (int y = 1; y <= runs; y++) {
                    genMin = 0;
                    print(display,  "Run " + y + "\n");

                // create the initial population of chromosomes
                    chromosomes = new Chromosome[populationSize];
                    for (int x = 0; x < populationSize; x++) {
                        chromosomes[x] = new Chromosome(cities);
                    }

                    generation = 0;
                    double thisCost = 0.0;

                    while (generation < 100) {
                        evolve();
                        if(generation % 5 == 0 ) 
                            cities = MoveCities(originalCities); //Move from original cities, so they only move by a maximum of one unit.
                        generation++;

                        Chromosome.sortChromosomes(chromosomes, populationSize);
                        double cost = chromosomes[0].getCost();
                        thisCost = cost;

                        if (thisCost < genMin || genMin == 0) {
                            genMin = thisCost;
                        }
                        
                        NumberFormat nf = NumberFormat.getInstance();
                        nf.setMinimumFractionDigits(2);
                        nf.setMinimumFractionDigits(2);

                        print(display, "Gen: " + generation + " Cost: " + (int) thisCost);

                        if(display) {
                            updateGUI();
                        }
                    }

                    writeLog(genMin + "");

                    if (genMin > max) {
                        max = genMin;
                    }

                    if (genMin < min || min == 0) {
                        min = genMin;
                    }

                    sum +=  genMin;

                    print(display, "");
                }

                avg = sum / runs;
                print(display, "Statistics after " + runs + " runs");
                print(display, "Solution found after " + generation + " generations." + "\n");
                print(display, "Statistics of minimum cost from each run \n");
                print(display, "Lowest: " + min + "\nAverage: " + avg + "\nHighest: " + max + "\n");
                print(display, "Fitness grade: " + 60 * 2868.0/avg );
            } catch (NumberFormatException e) {
                System.out.println("Please ensure you enter integers for cities and population size");
                System.out.println(formatMessage);
            }
        }
    }
}
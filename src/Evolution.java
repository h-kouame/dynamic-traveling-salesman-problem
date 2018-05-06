import java.util.Arrays;
import java.util.Random;
import com.google.common.primitives.Ints;

class Evolution{
	protected static int tournamentSize = 8;
	
	/**
	 * Evolve given population to produce the next generation.
	 * @param population The population to evolve.
	 * @param cityList List of cities, needed for the Chromosome constructor calls you will be doing when mutating and breeding Chromosome instances
	 * @return The new generation of individuals.
	 */
   public static Chromosome [] Evolve(Chromosome [] population, City [] cityList){
      Chromosome [] parents = SelectParents(population, tournamentSize);
      Chromosome [] newPopulation = new Chromosome [population.length];
      Chromosome parent1, parent2;
	  for (int i = 0; i < population.length; i +=2){
		  parent1 = parents[i/2];
		  parent2 = parents[parents.length - i/2 - 1];
		  newPopulation[i] = CrossoverRight(parent1, parent2, cityList);
		  newPopulation[i + 1] = CrossoverLeft(parent1, parent2, cityList);
      }
      return newPopulation;
   }
   
	public static Chromosome [] SelectParents(Chromosome [] population, int tournamentSize) {
	  int populationSize = population.length;
	  int newPopulationSize = populationSize/2;
      Chromosome [] newPopulation = new Chromosome[newPopulationSize];
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
	
	
	public static Chromosome CrossoverRight(Chromosome parent1, Chromosome parent2, City [] cityList){
		int [] parent1CityIndexes = parent1.getCities();
		int [] parent2CityIndexes = parent2.getCities();
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
 	    		city = parent1CityIndexes[nextIndex1];
 	    		newCityList[i] = city;
 	    		usedCities[i] = city;
 	    		currentIndex1 = nextIndex1;
 	    		currentIndex2 = nextIndex2;
 	    		continue;
 		   	}
 
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
 	 	    
// 	 	    Take closest right city
 	 	    City contestant1;
 	 	    City contestant2;	
 	    	contestant1 = cityList[parent1CityIndexes[nextIndex1]]; 
 	    	contestant2 = cityList[parent2CityIndexes[nextIndex2]]; 

 	    	City currentCity = cityList[newCityList[i - 1]];
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

 	    Chromosome offspring = new Chromosome(newCityList, cityList);
        return offspring;
	}
	
	
	public static Chromosome CrossoverLeft(Chromosome parent1, Chromosome parent2, City [] cityList){
		int [] parent1CityIndexes = parent1.getCities();
		int [] parent2CityIndexes = parent2.getCities();
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
 	    		city = parent1CityIndexes[nextIndex1];
 	    		newCityList[i] = city;
 	    		usedCities[i] = city;
 	    		currentIndex1 = nextIndex1;
 	    		currentIndex2 = nextIndex2;
 	    		continue;
 		   	}
 
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
 	    	contestant1 = cityList[parent1CityIndexes[previousIndex1]]; 
 	    	contestant2 = cityList[parent2CityIndexes[previousIndex2]]; 

 	    	City currentCity = cityList[newCityList[i - 1]];
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
 	    Chromosome offspring = new Chromosome(newCityList, cityList);
        return offspring;
	}
	
	/**
	 * The method used to generate a mutant of a chromosome
	 * @param original The chromosome to mutate.
	 * @param cityList list of cities, needed to instantiate the new Chromosome.
	 * @return Mutated chromosome.
	 */
	public static Chromosome Mutate(Chromosome parent, City [] cities){
      int [] cityList = parent.getCities();
      Random randomGenerator = new Random();
//      Choose two cities randomly to swap their position
      int city1Index = randomGenerator.nextInt(cityList.length);
      int city2Index = randomGenerator.nextInt(cityList.length);;
      int city1 = cityList[city1Index];
      int city2 = cityList[city2Index];
      parent.setCity(city1Index, city2);
      parent.setCity(city2Index, city1);
      parent.calculateCost(cities);
      return parent;
    }
	
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

}
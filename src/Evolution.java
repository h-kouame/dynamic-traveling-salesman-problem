class Evolution{
	/**
	 * The method used to generate a mutant of a chromosome
	 * @param original The chromosome to mutate.
	 * @param cityList list of cities, needed to instantiate the new Chromosome.
	 * @return Mutated chromosome.
	 */
	public static Chromosome Mutate(Chromosome original, City [] cityList){
      int [] cityIndexes = original.getCities();
      City [] newCities = new City[cityIndexes.length];
      
      for (int i = 0; i<cityIndexes.length; ++i){
         newCities[i] = cityList[cityIndexes[i]];
      }
      
      return new Chromosome(newCities);
   }
	
	/**
	 * Breed two chromosomes to create a offspring
	 * @param parent1 First parent.
	 * @param parent2 Second parent.
	 * @param cityList list of cities, needed to instantiate the new Chromosome.
	 * @return Chromosome resuling from breeding parent.
	 */
	public static Chromosome Breed(Chromosome parent1, Chromosome parent2, City [] cityList){
	      int [] cityIndexes = parent1.getCities();
	      City [] newCities = new City[cityIndexes.length];
	      
	      for (int i = 0; i<cityIndexes.length; ++i){
	         newCities[i] = cityList[cityIndexes[i]];
	      }
	      
	      return new Chromosome(newCities);
	   }

	/**
	 * Evolve given population to produce the next generation.
	 * @param population The population to evolve.
	 * @param cityList List of ciies, needed for the Chromosome constructor calls you will be doing when mutating and breeding Chromosome instances
	 * @return The new generation of individuals.
	 */
   public static Chromosome [] Evolve(Chromosome [] population, City [] cityList){
      Chromosome [] newPopulation = new Chromosome [population.length];
      for (int i = 0; i<population.length; ++i){
         newPopulation[i] = Mutate(population[i], cityList);
      }
      
      return newPopulation;
   }
}
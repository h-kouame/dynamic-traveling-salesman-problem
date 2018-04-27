SRCDIR = src
BINDIR = bin

JC = javac
J = java
JFLAGS = -g -d $(BINDIR) -cp $(BINDIR)

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        $(SRCDIR)/City.java \
        $(SRCDIR)/Chromosome.java \
        $(SRCDIR)/Evolution.java \
        $(SRCDIR)/TSP.java 

default: classes

TSP.class: TSP.java Evolution.java Chromosome.class City.class
Evolution.class: Evolution.java Chromosome.class City.class
Chromosome: Chromosome.java City.class
City.class: City.java

run:
	$(J) -cp $(BINDIR) TSP 100

gui:
	$(J) -cp $(BINDIR) TSP 100 y
	
classes: $(CLASSES:.java=.class)
	
clean:
	$(RM) $(BINDIR)/*.class
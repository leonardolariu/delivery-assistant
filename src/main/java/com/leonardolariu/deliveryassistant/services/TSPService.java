package com.leonardolariu.deliveryassistant.services;

import com.leonardolariu.deliveryassistant.services.utils.Candidate;
import com.leonardolariu.deliveryassistant.services.utils.Package;
import com.leonardolariu.deliveryassistant.services.utils.Population;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
class TSPService {
    private final int RUNS = 30;
    private final int MAX_ITERATIONS_SINCE_NO_CHANGE = 200;
    private final double MUTATION_PROBABILITY = 0.05;
    private final double CROSSOVER_PROBABILITY = 0.15;

    private List<Package> unorderedPackages;

    List<Package> solve(List<Package> unorderedPackages) {
        this.unorderedPackages = unorderedPackages;
        Candidate globalBestCandidate = new Candidate(unorderedPackages.size());

        for (int i = 1; i <= RUNS; ++i) {
            Candidate runBestCandidate = geneticAlgorithm();
            if (runBestCandidate.getFitness() > globalBestCandidate.getFitness())
                globalBestCandidate = runBestCandidate;
        }
        globalBestCandidate = hillClimbBestImprovement(globalBestCandidate);

        List<Package> orderedPackages = new ArrayList<>();
        globalBestCandidate.getIndexes().forEach(i-> orderedPackages.add(unorderedPackages.get(i)));

        orderedPackages.sort(Comparator.comparing(Package::getOrder));
        int curr = 0;
        for (Package aPackage: orderedPackages) aPackage.setOrder(curr++);

        return orderedPackages;
    }

    private Candidate geneticAlgorithm() {
        Population population = new Population(unorderedPackages);
        population.evaluate();
        Candidate runBestCandidate = population.getBestCandidate();

        int iterationsSinceNoChange = 0;
        while (iterationsSinceNoChange < MAX_ITERATIONS_SINCE_NO_CHANGE) {
            population.selectPopulation(); // select P(i) from P(i-1)

            //alter P(i)
            population.crossOver(CROSSOVER_PROBABILITY);
            population.mutate(MUTATION_PROBABILITY);

            population.evaluate();
            Candidate iterationBestCandidate = population.getBestCandidate();
            if (iterationBestCandidate.getFitness() > runBestCandidate.getFitness()) {
                iterationsSinceNoChange = 0;
                runBestCandidate = iterationBestCandidate;
            } else ++iterationsSinceNoChange;
        }

        return runBestCandidate;
    }

    private Candidate hillClimbBestImprovement(Candidate candidate) {
        List<Integer> indexes = candidate.getIndexes();
        int indexesSize = indexes.size();

        double bestFitness = candidate.getFitness(), currFitness;
        int bestI = -1, bestJ = -1;
        boolean foundImprovement = false;
        for (int i = 0; i < indexesSize - 1; ++i) {
            for (int j = i+1; j <indexesSize; ++j) {
                Collections.swap(indexes, i, j);
                currFitness = computeCandidateFitness(indexes, indexesSize);

                if (currFitness > bestFitness) {
                    bestFitness = currFitness;
                    bestI = i;
                    bestJ = j;
                    foundImprovement = true;
                }

                Collections.swap(indexes, i, j);
            }
        }

        if (!foundImprovement)
            return candidate;

        Collections.swap(indexes, bestI, bestJ);
        Candidate bestCandidate = new Candidate(indexes, bestFitness);
        return hillClimbBestImprovement(bestCandidate);
    }

    private double computeCandidateFitness(List<Integer> indexes, int indexesSize) {
        double routeDistance = 0;
        for (int i = 0; i < indexesSize - 1; ++i) {
            Package pack1 = unorderedPackages.get(indexes.get(i));
            Package pack2 = unorderedPackages.get(indexes.get(i+1));

            routeDistance += DistanceService.euclidianDistance(
                    pack1.getXCoordinate(), pack1.getYCoordinate(),
                    pack2.getXCoordinate(), pack2.getYCoordinate()
            );
        }

        // assure that shorter length determines greater fitness
        return 1D / routeDistance;
    }
}

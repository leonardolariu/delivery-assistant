package com.leonardolariu.deliveryassistant.services;

import com.leonardolariu.deliveryassistant.services.utils.Candidate;
import com.leonardolariu.deliveryassistant.services.utils.Package;
import com.leonardolariu.deliveryassistant.services.utils.Population;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Slf4j
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
}

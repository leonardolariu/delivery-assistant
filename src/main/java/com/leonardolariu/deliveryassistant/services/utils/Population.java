package com.leonardolariu.deliveryassistant.services.utils;

import com.leonardolariu.deliveryassistant.services.DistanceService;

import java.util.*;

public class Population {
    private final int POPULATION_SIZE = 500, ELITES_SIZE = 50;
    private int candidateSize;
    private List<Package> packages;
    private Map<Integer, Candidate> candidates = new HashMap<>();
    private Candidate bestCandidate;

    private double normalizationFactor; // used to reduce any probability function
    // to a probability density function with total probability of one

    public Population(List<Package> unorderedPackages) {
        this.packages = unorderedPackages;
        this.candidateSize = packages.size();
        this.normalizationFactor = 0D;

        for (int i = 0; i < POPULATION_SIZE; ++i)
            candidates.put(i, new Candidate(candidateSize));
    }



    public void evaluate() {
        bestCandidate = new Candidate(candidateSize);

        // prepare the fortune wheel
        for (Candidate candidate: candidates.values()) {
            List<Integer> indexes = candidate.getIndexes();
            double routeDistance = 0;

            // compute candidate fitness
            for (int i = 0; i < candidateSize - 1; ++i) {
                Package pack1 = packages.get(indexes.get(i));
                Package pack2 = packages.get(indexes.get(i+1));

                routeDistance += DistanceService.euclidianDistance(
                        pack1.getXCoordinate(), pack1.getYCoordinate(),
                        pack2.getXCoordinate(), pack2.getYCoordinate()
                );
            }

            // assure that shorter length determines greater fitness
            double candidateFitness = 1D / routeDistance;

            candidate.setFitness(candidateFitness);
            normalizationFactor += candidateFitness;
            if (candidateFitness > bestCandidate.getFitness())
                bestCandidate = candidate;
        }

        // set the starting point on the "wheel" for every individual
        for (int i = 1; i < POPULATION_SIZE; ++i) { // skip first
            double previousWheelStart = candidates.get(i-1).getWheelStart();
            candidates.get(i).setWheelStart(previousWheelStart, normalizationFactor);
        }

    }

    public Candidate getBestCandidate() {
        return new Candidate(bestCandidate);
    }

    public void selectPopulation() {
        List<Candidate> bestCandidates = new ArrayList<>(candidates.values());
        bestCandidates.sort((c1, c2) -> c2.getFitness().compareTo(c1.getFitness()));

        Map<Integer, Candidate> newCandidates = new HashMap<>();
        for (int i = 0; i < ELITES_SIZE; ++i) {
            Candidate newCandidate = new Candidate(bestCandidates.get(i));
            newCandidates.put(i, newCandidate);
        }

        for (int i = ELITES_SIZE; i < POPULATION_SIZE; ++i) {
            double fortune = Math.random();
            int luckyCandidateIndex = binarySearch(0, POPULATION_SIZE-1, fortune);

            Candidate luckyCandidate = candidates.get(luckyCandidateIndex);
            newCandidates.put(i, new Candidate(luckyCandidate));
        }

        candidates = newCandidates;
        normalizationFactor = 0;
    }

    private int binarySearch(int left, int right, double fortune) {
        if (left > right) return -1;

        int mid = (left + right) / 2;
        Candidate midCandidate = candidates.get(mid);

        if (fortune >= midCandidate.getWheelStart() &&
                (mid == POPULATION_SIZE - 1 || fortune < candidates.get(mid+1).getWheelStart()))
                return mid;

        if (fortune < midCandidate.getWheelStart())
            return binarySearch(left, mid-1, fortune);

        return binarySearch(mid+1, right, fortune);
    }

    public void crossOver(double crossOverProbability) {
        List<Integer> parentIndex = new ArrayList<>();
        for (int i = 0; i < 2; ++i) parentIndex.add(i);
        int parentCount = 1;

        Random rand = new Random();
        for (int i = 0; i < POPULATION_SIZE; ++i) {
            if (Math.random() < crossOverProbability) {
                parentCount = parentCount ^ 1;
                parentIndex.set(parentCount, i);

                if (parentCount == 0)
                    continue;

                int cut1 = rand.nextInt(candidateSize), cut2, temp;
                do {
                    cut2 = rand.nextInt(candidateSize);
                } while (cut1 == cut2);

                if (cut1 > cut2) {
                    temp = cut1;
                    cut1 = cut2;
                    cut2 = temp;
                }

                Candidate candidate1 = candidates.get(parentIndex.get(0));
                Candidate candidate2 = candidates.get(parentIndex.get(1));

                List<Integer> indexes1 = candidate1.getIndexes();
                List<Integer> indexes2 = candidate2.getIndexes();

                List<Integer> newIndexes1 = buildIndexes(indexes1, indexes2, cut1, cut2);
                List<Integer> newIndexes2 = buildIndexes(indexes2, indexes1, cut1, cut2);

                candidate1.setIndexes(newIndexes1);
                candidate2.setIndexes(newIndexes2);
            }
        }
    }

    private List<Integer> buildIndexes(List<Integer> indexes1, List<Integer> indexes2, int cut1, int cut2) {
        Map<Integer, Boolean> used = new HashMap<>();
        for (int i = cut1; i <= cut2; ++i)
            used.put(indexes1.get(i), true);

        int size = indexes1.size();
        List<Integer> indexes = new ArrayList<>(size);

        for (int i = 0; i < size; ++i) {
            if (i >= cut1 && i <= cut2) indexes.add(indexes1.get(i));
            else indexes.add(0);

        }

        int currPos = 0;
        for (int i = 0; i < size; ++i) {
            if (currPos == cut1) currPos = cut2+1;
            if (!used.containsKey(indexes2.get(i)))
                indexes.set(currPos++, indexes2.get(i));
        }

        return indexes;
    }

    public void mutate(double mutationProbability) {
        for (Candidate candidate: candidates.values()) {
            if (Math.random() < mutationProbability)
                candidate.mutate();
        }
    }
}

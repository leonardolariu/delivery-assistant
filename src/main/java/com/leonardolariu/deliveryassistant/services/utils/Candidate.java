package com.leonardolariu.deliveryassistant.services.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public class Candidate {
    private List<Integer> indexes = new ArrayList<>();
    private Double fitness = Double.MIN_VALUE;
    private double wheelStart = 0D;

    public Candidate(int candidateSize) {
        for (int i = 0; i < candidateSize; ++i) indexes.add(i);
        Collections.shuffle(indexes);
    }

    public Candidate(List<Integer> indexes, Double fitness) {
        this.indexes = indexes;
        this.fitness = fitness;
    }

    Candidate(Candidate candidate) {
        indexes = candidate.getIndexes();
        fitness = candidate.getFitness();
    }

    void setWheelStart(double previousWheelStart, double normalizationFactor) {
        wheelStart = previousWheelStart + fitness / normalizationFactor;
    }

    void mutate() {
        Random rand = new Random();
        int size = indexes.size();
        int cut1 = rand.nextInt(size), cut2;
        do {
            cut2 = rand.nextInt(size);
        } while (cut1 == cut2);

        Collections.swap(indexes, cut1, cut2);
    }
}

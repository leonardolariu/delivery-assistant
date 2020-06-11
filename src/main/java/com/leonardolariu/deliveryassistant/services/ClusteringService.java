package com.leonardolariu.deliveryassistant.services;

import com.leonardolariu.deliveryassistant.services.utils.Centroid;
import com.leonardolariu.deliveryassistant.services.utils.Package;
import com.leonardolariu.deliveryassistant.services.utils.RandomCollection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@EnableCaching
class ClusteringService {
    private final int maxIterations = 100;

    @Cacheable("estimations")
    int estimateDriversCount(List<Package> packages, int maxK) {
        if (maxK == 1)
            return 1;

        double bestScore = Double.MIN_VALUE;
        int bestK = 1;
        for (int k = 2; k <= maxK; ++k) {
            Map<Centroid, List<Package>> clusters = kMeansPlusPlus(packages, k);

            double currScore = 0;
            for (Centroid centroid : clusters.keySet()) {
                List<Centroid> otherCentroids = clusters.keySet().stream()
                        .filter(currCentroid -> !currCentroid.equals(centroid))
                        .collect(toList());

                for (Package currPackage : clusters.get(centroid)) {
                    Centroid secondNeareast = nearestCentroid(currPackage, otherCentroids);

                    double aDist = clusters.get(centroid).stream()
                            .map(aPackage -> computeDistance(currPackage, aPackage))
                            .reduce((double) 0, Double::sum);

                    double bDist = clusters.get(secondNeareast).stream()
                            .map(aPackage -> computeDistance(currPackage, aPackage))
                            .reduce((double) 0, Double::sum);

                    double silhouetteScore = (bDist - aDist) / Double.max(aDist, bDist);
                    currScore += silhouetteScore;
                }
            }

            currScore /= packages.size();
            log.info("k = " + k + " -> silhouetteScore = " + currScore);
            
            if (currScore > bestScore) {
                bestScore = currScore;
                bestK = k;
            }
        }

        return bestK;
    }

    @Cacheable("routes")
    Map<Centroid, List<Package>> kMeansPlusPlus(List<Package> packages, int k) {

        List<Centroid> centroids = smartlyDistributedCentroids(packages, k);
        Map<Centroid, List<Package>> clusters = new HashMap<>();
        Map<Centroid, List<Package>> lastState = new HashMap<>();

        // iterate for a pre-defined number of times
        for (int i = 0; i < maxIterations; i++) {
            boolean isLastIteration = i == maxIterations - 1;

            // in each iteration we should find the nearest centroid for each record
            for (Package currPackage : packages) {
                Centroid centroid = nearestCentroid(currPackage, centroids);
                assignToCluster(clusters, currPackage, centroid);
            }

            // if the assignments do not change, then the algorithm terminates
            boolean shouldTerminate = isLastIteration || clusters.equals(lastState);
            lastState = clusters;
            if (shouldTerminate) {
                break;
            }

            // at the end of each iteration we should relocate the centroids
            centroids = relocateCentroids(clusters);
            clusters = new HashMap<>();
        }

        return lastState;
    }

    private List<Centroid> smartlyDistributedCentroids(List<Package> packages, int k) {
        List<Centroid> centroids = new ArrayList<>();

        int randomIndex = ThreadLocalRandom.current().nextInt(0, packages.size());
        centroids.add(new Centroid(
                packages.get(randomIndex).getXCoordinate(),
                packages.get(randomIndex).getYCoordinate())
        );

        for (int i = 2; i <= k; ++i) {
            RandomCollection<Package> randomCollection = new RandomCollection<>();
            List<Double> weights = new ArrayList<>();
            Centroid nearest;
            double weightsSum = 0;

            for (Package currPackage : packages) {
                nearest = nearestCentroid(currPackage, centroids);
                double distanceToNearest = computeDistance(currPackage, nearest);
                weights.add(Math.pow(distanceToNearest, 2));
                weightsSum += Math.pow(distanceToNearest, 2);
            }

            for (int j = 0; j < packages.size(); ++j) {
                // we need to normalize the weights
                randomCollection.add(weights.get(j) / weightsSum, packages.get(j));
            }

            Package randomPackage = randomCollection.next();
            centroids.add(new Centroid(randomPackage.getXCoordinate(), randomPackage.getYCoordinate()));
        }

        return centroids;
    }

    private Centroid nearestCentroid(Package currPackage, List<Centroid> centroids) {
        double minimumDistance = Double.MAX_VALUE;
        Centroid nearest = null;

        for (Centroid centroid : centroids) {
            double currentDistance = computeDistance(currPackage, centroid);
            if (currentDistance < minimumDistance) {
                minimumDistance = currentDistance;
                nearest = centroid;
            }
        }

        return nearest;
    }

    private void assignToCluster(Map<Centroid, List<Package>> clusters, Package currPackage, Centroid centroid) {
        clusters.compute(centroid, (key, list) -> {
            if (list == null) {
                list = new ArrayList<>();
            }

            list.add(currPackage);
            return list;
        });
    }

    private List<Centroid> relocateCentroids(Map<Centroid, List<Package>> clusters) {
        return clusters.entrySet().stream().map(e -> average(e.getKey(), e.getValue())).collect(toList());
    }

    private Centroid average(Centroid centroid, List<Package> packages) {
        if (packages == null || packages.isEmpty()) {
            return centroid;
        }

        double xAverage = 0, yAverage = 0;
        for (Package currPackage : packages) {
            xAverage += currPackage.getXCoordinate();
            yAverage += currPackage.getYCoordinate();
        }

        xAverage /= packages.size();
        yAverage /= packages.size();
        return new Centroid(xAverage, yAverage);
    }



    private double computeDistance(Package currPackage, Centroid centroid) {
        return DistanceService.euclidianDistance(currPackage.getXCoordinate(), currPackage.getYCoordinate(),
                        centroid.getXCoordinate(), centroid.getYCoordinate());
    }

    private double computeDistance(Package currPackage, Package otherPackage) {
        if (currPackage.equals(otherPackage))
            return 0;

        return DistanceService.euclidianDistance(currPackage.getXCoordinate(), currPackage.getYCoordinate(),
                otherPackage.getXCoordinate(), otherPackage.getYCoordinate());
    }
}

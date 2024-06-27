package org.example.algovisualiser;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.application.Platform;


import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class VisualAlgoController {
    public int speed = 50; // Pause zwischen den Vergleichen in ms
    public int size = 250; // Größe des Arrays
    private Thread currentSortingThread;
    private SoundPlayer soundPlayer;
    private boolean finished = false;
    private Thread sortingThread;

    @FXML
    ComboBox<String> comboBox = new ComboBox<>();
    @FXML
    Pane pane = new Pane();
    @FXML
    Label label = new Label();
    @FXML
    Label sortLabel = new Label();
    @FXML
    Label comparisonLabel = new Label();
    @FXML
    Label arrayLabel = new Label();
    @FXML
    Slider arrayGrößeSlider = new Slider();
    @FXML
    Slider zeitSlider = new Slider();
    @FXML
    ListView listView = new ListView();

    @FXML
    public void initialize() {
        comboBox.getItems().addAll("Bubble Sort", "Insertion Sort", "Merge Sort", "Quick Sort", "Select Sort");
        comboBox.setOnAction(this::onComboBoxClick);
        soundPlayer = new SoundPlayer("sound.mp3");
    }

    @FXML
    private void onComboBoxClick(ActionEvent event) {
        stopSorting();
        stopQuickSorting();
        speed = (int) zeitSlider.getValue();
        size = (int) arrayGrößeSlider.getValue();
        if (currentSortingThread != null && currentSortingThread.isAlive()) {
            currentSortingThread.interrupt();
        }
        int[] array = generateRandomArray(size);
        displayArray(array, -1);
        label.setVisible(false);
        listView.setVisible(false);
        arrayLabel.setVisible(true);

        switch (comboBox.getValue()) {
            case "Bubble Sort":
                currentSortingThread = createSortThread(() -> bubbleSort(array));
                sortLabel.setVisible(true);
                sortLabel.setText("Bubble Sort");
                break;
            case "Insertion Sort":
                currentSortingThread = createSortThread(() -> insertionSort(array));
                sortLabel.setVisible(true);
                sortLabel.setText("Insertion Sort");
                break;
            case "Merge Sort":
                currentSortingThread = createSortThread(() -> startMergeSort(array));
                sortLabel.setVisible(true);
                sortLabel.setText("Merge Sort");
                break;
            case "Quick Sort":
                currentSortingThread = createSortThread(() -> startQuickSort(array));
                sortLabel.setVisible(true);
                sortLabel.setText("Quick Sort");
                break;
            case "Select Sort":
                currentSortingThread = createSortThread(() -> selectSort(array));
                sortLabel.setVisible(true);
                sortLabel.setText("Select Sort");
                break;
            /*case "Heap Sort":
                break;*/
        }
    }


    private Thread createSortThread(Runnable sortMethod) {
        Thread thread = new Thread(sortMethod);
        thread.setDaemon(true);
        thread.start();
        return thread;
    }


    //Anzeige Updaten
    public void displayArray(int[] array, int currentIndex) {
        pane.getChildren().clear();
        double barWidth = pane.getWidth() / array.length;
        for (int i = 0; i < array.length; i++) {
            double height = (array[i] / (double) findMax(array)) * pane.getHeight();
            Rectangle rect = new Rectangle(barWidth, height);
            rect.setTranslateX(i * barWidth);
            rect.setTranslateY(pane.getHeight() - height);
            rect.setId("rect");
            rect.setStroke(Color.BLACK);
            rect.setStrokeWidth(1);

            if (i == currentIndex || i == currentIndex - 1) {
                rect.setFill(Color.RED); //Farbe des Balkens welcher gerade sortiert wird
            } else {
                rect.setFill(Color.rgb(39, 8, 117));  //Farbe der anderen Balken
            }

            pane.getChildren().add(rect);
        }

        int maxValue = Arrays.stream(array).max().getAsInt();
        if (currentIndex >= 0) {
            soundPlayer.playSound(array[currentIndex], maxValue, speed);

        }
    }


    // Debug Array Anzeige Liste
    public void updateArrayDisplay(int[] array, int currentIndex) {
        StringBuilder displayText = new StringBuilder("Werte um Index " + currentIndex + ": ");

        int start = Math.max(0, currentIndex - 3);
        int end = Math.min(array.length - 1, currentIndex + 3);

        for (int i = start; i <= end; i++) {
            if (i == currentIndex) {
                displayText.append("[" + array[i] + "] ");
            } else {
                displayText.append(array[i] + " ");
            }
        }

        arrayLabel.setText(displayText.toString());
    }


    private int findMax(int[] array) {
        int max = Integer.MIN_VALUE;
        for (int value : array) {
            if (value > max) max = value;
        }
        return max;
    }

    public int[] generateRandomArray(int size) {
        Random rand = new Random();
        int[] array = new int[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = rand.nextInt((int) pane.getHeight());
        }
        return array;
    }

    public void bubbleSort(int[] array) {
            boolean swapped;
            int totalComparisons = 0;
            do {
                if (Thread.currentThread().isInterrupted()) return;
                swapped = false;
                for (int i = 1; i < array.length; i++) {
                    final int currentIndex = i;
                    final int currentComparisons = totalComparisons + 1;

                    Platform.runLater(() -> {
                        displayArray(array, currentIndex);
                        updateArrayDisplay(array, currentIndex);
                        setComparisonLabel(currentComparisons);
                    });
                    pause();

                    if (array[i - 1] > array[i]) {
                        int temp = array[i];
                        array[i] = array[i - 1];
                        array[i - 1] = temp;
                        swapped = true;
                    }
                    totalComparisons++;
                }
            } while (swapped);

            final int finalComparisons = totalComparisons;
            Platform.runLater(() -> {
                label.setVisible(true);
                listView.setVisible(true);
                arrayLabel.setVisible(false);
                listView.setItems(FXCollections.observableArrayList(Arrays.stream(array).boxed().collect(Collectors.toList())));
                setComparisonLabel(finalComparisons);
            });
    }


    public void selectSort(int[] array) {
        int totalComparisons = 0;
            if (Thread.currentThread().isInterrupted()) return;
            int q, k;
            for (int i = array.length - 1; i >= 1; i--) {
                final int currentIndex = i;
                final int currentComparisons = totalComparisons + 1;
                q = 0;

                Platform.runLater(() -> {
                    displayArray(array, currentIndex);
                    updateArrayDisplay(array, currentIndex);
                    setComparisonLabel(currentComparisons);
                });
                pause();

                for (int j = 1; j <= i; j++) {
                    if (array[j] > array[q]) {
                        totalComparisons++;
                        q = j;
                    }
                }
                k = array[q];
                array[q] = array[i];
                array[i] = k;
            }

        final int finalComparisons = totalComparisons;
            if (!Thread.currentThread().isInterrupted()) {
                Platform.runLater(() -> {
                    label.setVisible(true);
                    listView.setVisible(true);
                    arrayLabel.setVisible(false);
                    listView.setItems(FXCollections.observableArrayList(Arrays.stream(array).boxed().collect(Collectors.toList())));
                    setComparisonLabel(finalComparisons);
                });
            }
    }

    public void insertionSort(int[] array) {
            int totalComparisons = 0;
            for (int i = 1; i < array.length; ++i) {
                int temp = array[i];
                int j = i - 1;

                final int currentIndex = i;
                Platform.runLater(() -> {
                    displayArray(array, currentIndex - 1);
                    updateArrayDisplay(array, currentIndex);
                });

                while (j >= 0 && array[j] > temp) {
                    if (Thread.currentThread().isInterrupted()) return;
                    totalComparisons++;
                    array[j + 1] = array[j];
                    j = j - 1;


                    final int currentComparisons = totalComparisons;
                    final int _j = j + 1;
                    Platform.runLater(() -> {
                        updateArrayDisplay(array, _j);
                        displayArray(array, _j);
                        setComparisonLabel(currentComparisons);
                    });
                    pause();
                }
                array[j + 1] = temp;
            }

            final int finalComparisons = totalComparisons;
                Platform.runLater(() -> {
                    displayArray(array, -1);
                    label.setVisible(true);
                    listView.setVisible(true);
                    arrayLabel.setVisible(false);
                    listView.setItems(FXCollections.observableArrayList(Arrays.stream(array).boxed().collect(Collectors.toList())));
                    setComparisonLabel(finalComparisons);
                });
    }

    public void setComparisonLabel(int comparisonCount) {
        Platform.runLater(() -> comparisonLabel.setText("Vergleiche: " + comparisonCount));
    }

    private void pause() {
        try {
            Thread.sleep(speed);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }





    //
    // https://javabeginners.de/Algorithmen/Sortieralgorithmen/Mergesort.php
    //





    public void mergeSort(int[] array, int left, int right, int[] totalComparisons) {
        if (left < right) {
            int middle = (left + right) / 2;

                mergeSort(array, left, middle, totalComparisons);
                mergeSort(array, middle + 1, right, totalComparisons);

                merge(array, left, middle, right, totalComparisons);
        }
    }

    public void merge(int[] array, int left, int middle, int right, int[] totalComparisons) {
        int n1 = middle - left + 1;
        int n2 = right - middle;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; ++i) {
            L[i] = array[left + i];
        }
        for (int j = 0; j < n2; ++j) {
            R[j] = array[middle + 1 + j];
        }

        int i = 0, j = 0;
        int k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                array[k] = L[i];
                i++;
            } else {
                array[k] = R[j];
                j++;
            }
            k++;

            totalComparisons[0]++;

            final int currentIndex = k;
            Platform.runLater(() -> {
                displayArray(array, currentIndex - 1);
                updateArrayDisplay(array, currentIndex);
            });
            pause();
        }

        while (i < n1) {
            array[k] = L[i];
            i++;
            k++;

            final int currentIndex = k;
            Platform.runLater(() -> {
                displayArray(array, currentIndex - 1);
                updateArrayDisplay(array, currentIndex);
            });
            pause();
        }

        while (j < n2) {
            array[k] = R[j];
            j++;
            k++;

            final int currentIndex = k;
            Platform.runLater(() -> {
                displayArray(array, currentIndex - 1);
                updateArrayDisplay(array, currentIndex);
            });
            pause();
        }

        Platform.runLater(() -> {
            setComparisonLabel(totalComparisons[0]);
        });
    }

    public void startMergeSort(int[] array) {
        int[] totalComparisons = {0};
        finished = false;
        sortingThread = new Thread(() -> {
            mergeSort(array, 0, array.length - 1, totalComparisons);
            finished = true;

            if (!Thread.currentThread().isInterrupted()) {
                Platform.runLater(() -> {
                    listView.setVisible(true);
                    listView.setItems(FXCollections.observableArrayList(Arrays.stream(array).boxed().collect(Collectors.toList())));
                    setComparisonLabel(totalComparisons[0]);
                    label.setVisible(true);
                    arrayLabel.setVisible(false);
                });
            }
        });
        sortingThread.start();
    }

    public void stopSorting() {
        if (sortingThread != null && sortingThread.isAlive()) {
            sortingThread.interrupt();
        }
    }




    //
    // https://javabeginners.de/Algorithmen/Sortieralgorithmen/Quicksort.php
    //



    public void quickSort(int[] array, int low, int high, int[] totalComparisons) {
        if (low < high) {
            int pi = partition(array, low, high, totalComparisons);
            quickSort(array, low, pi - 1, totalComparisons);
            quickSort(array, pi + 1, high, totalComparisons);
        }
    }

    public int partition(int[] array, int low, int high, int[] totalComparisons) {
        int pivot = array[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;

                final int currentIndex = i;
                final int currentComparisons = totalComparisons[0] + 1;
                Platform.runLater(() -> {
                    displayArray(array, currentIndex);
                    updateArrayDisplay(array, currentIndex);
                    setComparisonLabel(currentComparisons);
                });
                pause();
            }
            totalComparisons[0]++;
        }
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;

        final int currentIndex = i + 1;
        final int currentComparisons = totalComparisons[0];
        Platform.runLater(() -> {
            displayArray(array, currentIndex);
            updateArrayDisplay(array, currentIndex);
            setComparisonLabel(currentComparisons);
        });
        pause();

        return i + 1;
    }

    public void startQuickSort(int[] array) {
        int[] totalComparisons = {0};
        finished = false;
        sortingThread = new Thread(() -> {
            quickSort(array, 0, array.length - 1, totalComparisons);
            finished = true;

            if (!Thread.currentThread().isInterrupted()) {
                Platform.runLater(() -> {
                    listView.setVisible(true);
                    listView.setItems(FXCollections.observableArrayList(Arrays.stream(array).boxed().collect(Collectors.toList())));
                    setComparisonLabel(totalComparisons[0]);
                    label.setVisible(true);
                    arrayLabel.setVisible(false);
                });
            }
        });
        sortingThread.start();
    }

    public void stopQuickSorting() {
        if (sortingThread != null && sortingThread.isAlive()) {
            sortingThread.interrupt();
        }
    }
}
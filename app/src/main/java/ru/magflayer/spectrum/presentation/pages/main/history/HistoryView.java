package ru.magflayer.spectrum.presentation.pages.main.history;

import java.util.List;

import ru.magflayer.spectrum.domain.model.ColorPicture;

public interface HistoryView {

    void showHistory(List<ColorPicture> history);

}

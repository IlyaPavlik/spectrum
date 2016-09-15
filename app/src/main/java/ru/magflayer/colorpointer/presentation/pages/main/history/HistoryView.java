package ru.magflayer.colorpointer.presentation.pages.main.history;

import java.util.List;

import ru.magflayer.colorpointer.domain.model.ColorPicture;

public interface HistoryView {

    void showHistory(List<ColorPicture> history);

}

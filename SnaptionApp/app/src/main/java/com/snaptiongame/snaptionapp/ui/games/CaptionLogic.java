package com.snaptiongame.snaptionapp.ui.games;

import com.snaptiongame.snaptionapp.models.Caption;

import java.util.List;

/**
 * Extracts logic for the Cards to reduce coupling and promote testing.
 *
 * @author Cameron Geehr
 */
public class CaptionLogic {
    /**
     * Inserts the caption into the items list in the proper order.
     *
     * @param items The list of captions
     * @param caption The caption to insert
     */
    public static int insertCaption(List<Caption> items, Caption caption) {
        int index = 0;
        boolean added = false;

        while (index < items.size() && !added) {
            if (caption.compareTo(items.get(index)) < 0) {
                items.add(index, caption);
                added = true;
            }
            else {
                index++;
            }
        }
        if (!added) {
            items.add(caption);
        }
        return index;
    }


}

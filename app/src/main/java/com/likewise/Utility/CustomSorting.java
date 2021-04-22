package com.likewise.Utility;

import com.likewise.Model.MessageResponse;

import java.util.Comparator;

public class CustomSorting implements Comparator<MessageResponse> {

    @Override
    public int compare(MessageResponse o1, MessageResponse o2) {
        if(ServerTimeCalculator.getDateDifference(o1.getCreatedAt())>ServerTimeCalculator.getDateDifference(o2.getCreatedAt())) return -1;
        else if(ServerTimeCalculator.getDateDifference(o1.getCreatedAt())<ServerTimeCalculator.getDateDifference(o2.getCreatedAt())) return +1;
        else return 0;
    }
}

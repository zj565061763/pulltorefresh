package com.sd.demo.pulltorefresh.model;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/14.
 */

public class DataModel
{
    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public static List<DataModel> getListModel(int count)
    {
        final List<DataModel> listModel = new ArrayList<>();
        for (int i = 0; i < count; i++)
        {
            DataModel model = new DataModel();
            model.setName(String.valueOf(i));
            listModel.add(model);
        }
        return listModel;
    }
}

package io.taskmanager.api.model;

public class DevTaskModel extends BaseModel{

    private int dev_id;
    private int task_id;

    public DevTaskModel(int id) {
        super(id, null, null);
    }

    public int getDev_id() {
        return dev_id;
    }

    public int getTask_id() {
        return task_id;
    }

}

package io.metersphere.controller.request.testplan;

public class QueryTestPlanRequest extends TestPlanRequest {
    private String name;
    private String workspaceId;
    private boolean recent = false; // 表示查询最近的测试

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public boolean isRecent() {
        return recent;
    }

    public void setRecent(boolean recent) {
        this.recent = recent;
    }
}
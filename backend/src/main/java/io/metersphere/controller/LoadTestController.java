package io.metersphere.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.metersphere.base.domain.FileMetadata;
import io.metersphere.commons.constants.RoleConstants;
import io.metersphere.commons.utils.PageUtils;
import io.metersphere.commons.utils.Pager;
import io.metersphere.controller.request.ProjectRequest;
import io.metersphere.controller.request.testplan.*;
import io.metersphere.dto.LoadTestDTO;
import io.metersphere.service.FileService;
import io.metersphere.service.LoadTestService;
import io.metersphere.user.SessionUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping(value = "/testplan")
public class LoadTestController {
    @Resource
    private LoadTestService loadTestService;
    @Resource
    private FileService fileService;

    @GetMapping("recent/{count}")
    @RequiresRoles(value = {RoleConstants.TEST_MANAGER, RoleConstants.TEST_USER, RoleConstants.TEST_VIEWER}, logical = Logical.OR)
    public List<LoadTestDTO> recentTestPlans(@PathVariable int count) {
        String currentWorkspaceId = SessionUtils.getCurrentWorkspaceId();
        QueryTestPlanRequest request = new QueryTestPlanRequest();
        request.setWorkspaceId(currentWorkspaceId);
        PageHelper.startPage(1, count, true);
        return loadTestService.recentTestPlans(request);
    }

    @PostMapping("/list/{goPage}/{pageSize}")
    public Pager<List<LoadTestDTO>> list(@PathVariable int goPage, @PathVariable int pageSize, @RequestBody QueryTestPlanRequest request) {
        Page<Object> page = PageHelper.startPage(goPage, pageSize, true);
        request.setWorkspaceId(SessionUtils.getCurrentWorkspaceId());
        return PageUtils.setPageInfo(page, loadTestService.list(request));
    }

    @PostMapping(value = "/save", consumes = {"multipart/form-data"})
    public String save(
            @RequestPart("request") SaveTestPlanRequest request,
            @RequestPart(value = "file") MultipartFile file
    ) {
        return loadTestService.save(request, file);
    }

    @PostMapping(value = "/edit", consumes = {"multipart/form-data"})
    public String edit(
            @RequestPart("request") EditTestPlanRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return loadTestService.edit(request, file);
    }

    @GetMapping("/get/{testId}")
    public LoadTestDTO get(@PathVariable String testId) {
        return loadTestService.get(testId);
    }

    @PostMapping("/delete")
    public void delete(@RequestBody DeleteTestPlanRequest request) {
        loadTestService.delete(request);
    }

    @PostMapping("/run")
    public void delete(@RequestBody RunTestPlanRequest request) {
        loadTestService.run(request);
    }

    @GetMapping("/file/metadata/{testId}")
    public FileMetadata getFileMetadata(@PathVariable String testId) {
        return fileService.getFileMetadataByTestId(testId);
    }

    @PostMapping("/file/download")
    public ResponseEntity<byte[]> downloadJmx(@RequestBody FileOperationRequest fileOperationRequest) {
        byte[] bytes = fileService.loadFileAsBytes(fileOperationRequest.getId());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileOperationRequest.getName() + "\"")
                .body(bytes);
    }
}
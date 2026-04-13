package servlets;

import accounts.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExplorerServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserProfile user = getCurrentUser(req, resp);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String userRoot = getBasePath() + user.getLogin() + "/";
        Path rootPath = Paths.get(userRoot);

        if (!Files.exists(rootPath)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "User folder not found: " + userRoot);
            return;
        }

        String requestedPath = req.getPathInfo();
        if (requestedPath == null || requestedPath.equals("/")) {
            requestedPath = "";
        } else {
            requestedPath = requestedPath.substring(1);
        }
        requestedPath = URLDecoder.decode(requestedPath, "UTF-8");

        Path fullPath = rootPath.resolve(requestedPath);

        if (!Files.exists(fullPath)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "Path not found: " + fullPath);
            return;
        }

        Path realFullPath = fullPath.toRealPath();
        rootPath = rootPath.toRealPath();

        if (!realFullPath.startsWith(rootPath)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String userHome = "/explorer/" + user.getLogin() + "/";

        if (Files.isRegularFile(realFullPath)) {
            sendFile(realFullPath, resp);
            return;
        }

        if (Files.isDirectory(realFullPath)) {
            try (Stream<Path> dirItems = Files.list(realFullPath)) {
                List<Path> items = dirItems.collect(Collectors.toList());

                Comparator<Path> comparator = createComparators();
                items.sort(comparator);

                List<Map<String, Object>> fileList = new ArrayList<>();

                for (Path item : items) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", item.getFileName().toString());
                    fileInfo.put("isDirectory", Files.isDirectory(item));
                    fileInfo.put("isRegularFile", Files.isRegularFile(item));

                    if (Files.isRegularFile(item)) {
                        fileInfo.put("size", Files.size(item));
                    } else {
                        fileInfo.put("size", 0L);
                    }

                    fileInfo.put("lastModified", new Date(
                            Files.getLastModifiedTime(item).toMillis()));
                    fileList.add(fileInfo);
                }

                String parentPath = computeParentPath(requestedPath);

                req.setAttribute("generationTime", new Date());
                req.setAttribute("currentPath", requestedPath);
                req.setAttribute("parentPath", parentPath);
                req.setAttribute("items", fileList);
                req.setAttribute("userHome",userHome);

                req.getRequestDispatcher("/WEB-INF/explorer.jsp").forward(req, resp);
                return;

            } catch (IOException e) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Cannot read directory");
                return;
            }
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not a file or directory");
    }

    private void sendFile(Path filePath, HttpServletResponse resp) throws IOException {
        String fileName = filePath.getFileName().toString();
        long fileSize = Files.size(filePath);

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName + "\"");
        resp.setContentLengthLong(fileSize);

        try (InputStream in = Files.newInputStream(filePath);
             OutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    private String computeParentPath(String displayPath) {
        String parentPath;
        if (displayPath == null || displayPath.isEmpty()) {
            parentPath = null;
        } else {
            int lastSlash = displayPath.lastIndexOf('/');
            if (lastSlash == -1) {
                parentPath = "";
            } else {
                parentPath = displayPath.substring(0, lastSlash);
            }
        }
        return parentPath;
    }

    private Comparator<Path> createComparators() {
        Comparator<Path> byType = (a, b) -> {
            boolean aIsDir = Files.isDirectory(a, LinkOption.NOFOLLOW_LINKS);
            boolean bIsDir = Files.isDirectory(b, LinkOption.NOFOLLOW_LINKS);
            if (aIsDir && !bIsDir) return -1;
            if (!aIsDir && bIsDir) return 1;
            return 0;
        };

        Comparator<Path> byName = (a, b) ->
                a.getFileName().toString().
                        compareToIgnoreCase(b.getFileName().toString());
        return byType.thenComparing(byName);
    }
}
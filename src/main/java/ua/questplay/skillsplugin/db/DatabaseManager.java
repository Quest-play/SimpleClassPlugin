package ua.questplay.skillsplugin.db;

import ua.questplay.skillsplugin.SkillsPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseManager {
    private Connection connection;
    private final SkillsPlugin plugin;

    public DatabaseManager(SkillsPlugin plugin) {
        this.plugin = plugin;
    }


    public void connect() {
        try {
            String url = "jdbc:sqlite:plugins/SkillsPlugin/data.db"; // Path to db
            connection = DriverManager.getConnection(url);

            createTables();
            plugin.getLogger().info("Database connected!");
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    private void createTables() {
        String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                "uuid TEXT PRIMARY KEY," +
                "player_class TEXT NOT NULL," +
                "skills TEXT)";
        String sql1 = "CREATE TABLE IF NOT EXISTS skill_names (skill_name TEXT NOT NULL)";
        String virtual = "CREATE VIRTUAL TABLE IF NOT EXISTS player_data_search USING fts5(uuid, player_class, skills);";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
            try (PreparedStatement stmt2 = connection.prepareStatement(sql1)){
                stmt2.execute();
            }
            try (PreparedStatement stmt1 = connection.prepareStatement(virtual)) {
                stmt1.execute();
            }
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public void savePlayerClass(UUID uuid, String playerClass) {
        String sql = "INSERT INTO player_data(uuid, player_class, skills) VALUES (?, ?, ?) " +
                "ON CONFLICT(uuid) DO UPDATE SET player_class = excluded.player_class;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, playerClass);
            stmt.setString(3, ""); // skills по умолчанию — пустые
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.getMessage();
        }
    }


    public boolean hasSkill(UUID uuid, String skillName) {
        String sql = "SELECT skills FROM player_data WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String skills = rs.getString("skills");
                if (skills != null) {
                    List<String> skillList = Arrays.asList(skills.split(","));
                    return skillList.contains(skillName);
                }
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return false;
    }

    public boolean hasClass(UUID uuid) {
        String sql = "SELECT player_class FROM player_data WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String player_class = rs.getString("player_class");
                if (player_class != null) {
                    return !player_class.isEmpty();
                }
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return false;
    }

    public boolean hasPlayerClass(UUID uuid, String player_class) {
        String sql = "SELECT player_class, uuid FROM player_data WHERE uuid = ? AND player_class = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, player_class);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String player = rs.getString("player_class");
                if (player != null) {
                    return !player.isEmpty();
                }
            }
        } catch (SQLException e) {
            e.getMessage();
        }
        return false;
    }

    public boolean inTable(UUID uuid) {
        String sql = "SELECT uuid FROM player_data WHERE uuid = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String uuids = rs.getString("uuid");
                if (uuids != null) {
                    return uuids.contains(uuid.toString());
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return false;
    }

    public void addSkill(UUID uuid, String skillName) {
        String sqlSelect = "SELECT skills FROM player_data WHERE uuid = ?;";
        String sqlUpdate = "UPDATE player_data SET skills = ? WHERE uuid = ?;";

        try (PreparedStatement selectStmt = connection.prepareStatement(sqlSelect)) {
            selectStmt.setString(1, uuid.toString());
            ResultSet rs = selectStmt.executeQuery();

            String updatedSkills = skillName;
            if (rs.next()) {
                String currentSkills = rs.getString("skills");
                if (currentSkills != null && !currentSkills.isEmpty()) {
                    Set<String> skillsSet = new HashSet<>(Arrays.asList(currentSkills.split(",")));
                    skillsSet.add(skillName);
                    updatedSkills = String.join(",", skillsSet);
                }
            }

            try (PreparedStatement updateStmt = connection.prepareStatement(sqlUpdate)) {
                updateStmt.setString(1, updatedSkills);
                updateStmt.setString(2, uuid.toString());
                updateStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public void addSkillName(String skillName) {
        String sqlCheck = "SELECT skill_name FROM skill_names WHERE skill_name = ?";
        String sqlInsert = "INSERT INTO skill_names (skill_name) VALUES (?)";

        try (PreparedStatement checkStmt = connection.prepareStatement(sqlCheck)) {
            checkStmt.setString(1, skillName);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                try (PreparedStatement insertStmt = connection.prepareStatement(sqlInsert)) {
                    insertStmt.setString(1, skillName);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.getMessage();
        }
    }

    public String[] getSkillNames(String prefix) {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT skill_name FROM skill_names WHERE skill_name LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                skills.add(rs.getString("skill_name"));
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        return skills.toArray(new String[0]);
    }

    public String[] getAllSkillNames() {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT skill_name FROM skill_names";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                skills.add(rs.getString("skill_name"));
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        return skills.toArray(new String[0]);
    }

    public void deletePlayerData(UUID uuid) {
        String sql = "DELETE FROM player_data WHERE uuid = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.getMessage();
            e.printStackTrace();
        }

    }



    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            e.getMessage();
        }
    }
}
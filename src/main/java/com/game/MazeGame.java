package com.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 迷宫游戏主类
 * 实现迷宫加载、玩家移动和游戏逻辑
 */
public class MazeGame {

    // 迷宫字符常量
    private static final char WALL = '#';    // 墙壁
    private static final char PATH = ' ';    // 路径
    private static final char START = 'S';   // 起点
    private static final char EXIT = 'E';    // 出口
    private static final char PLAYER = 'X';  // 玩家位置标记

    // 迷宫尺寸限制
    private static final int MIN_SIZE = 5;   // 最小尺寸
    private static final int MAX_SIZE = 100; // 最大尺寸

    // 迷宫数据结构
    private char[][] maze;      // 二维数组存储迷宫
    private int width;          // 迷宫宽度
    private int height;         // 迷宫高度
    private int playerX;        // 玩家X坐标
    private int playerY;        // 玩家Y坐标
    private boolean gameOver;   // 游戏结束标志


    /**
     * 从文件加载迷宫
     * @param args 命令行参数（第一个参数应为迷宫文件名）
     * @throws IllegalArgumentException 如果文件无效或迷宫格式错误
     */
    public void loadMaze(String[] args) throws IllegalArgumentException {
        // 验证命令行参数
        if (args.length < 1) {
            throw new IllegalArgumentException("请提供迷宫文件");
        }

        try {
            File file = new File(args[0]);
            System.out.println("加载迷宫文件: " + file.getName());
            Scanner scanner = new Scanner(file);

            // 临时存储迷宫行
            List<String> mazeLines = new ArrayList<>();

            // 读取迷宫文件
            while (scanner.hasNextLine()) {
                mazeLines.add(scanner.nextLine());
            }
            scanner.close();

            // 验证迷宫尺寸
            height = mazeLines.size();
            if (height < MIN_SIZE || height > MAX_SIZE) {
                throw new IllegalArgumentException("迷宫高度必须在" + MIN_SIZE + "到" + MAX_SIZE + "之间");
            }

            width = mazeLines.get(0).length();
            if (width < MIN_SIZE || width > MAX_SIZE) {
                throw new IllegalArgumentException("迷宫宽度必须在" + MIN_SIZE + "到" + MAX_SIZE + "之间");
            }

            // 验证所有行长度一致
            for (String line : mazeLines) {
                if (line.length() != width) {
                    throw new IllegalArgumentException("迷宫必须为矩形，所有行长度相同");
                }
            }

            // 初始化迷宫数组
            maze = new char[height][width];
            boolean hasStart = false;
            boolean hasExit = false;

            // 解析迷宫内容
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    char c = mazeLines.get(y).charAt(x);

                    // 验证迷宫字符有效性
                    if (c != WALL && c != PATH && c != START && c != EXIT) {
                        throw new IllegalArgumentException("无效的迷宫字符: " + c);
                    }

                    maze[y][x] = c;

                    // 记录起点位置
                    if (c == START) {
                        if (hasStart) {
                            throw new IllegalArgumentException("迷宫只能有一个起点(S)");
                        }
                        hasStart = true;
                        playerX = x;
                        playerY = y;
                    }

                    // 记录出口位置
                    if (c == EXIT) {
                        if (hasExit) {
                            throw new IllegalArgumentException("迷宫只能有一个出口(E)");
                        }
                        hasExit = true;
                    }
                }
            }

            // 验证起点和出口存在
            if (!hasStart) {
                throw new IllegalArgumentException("迷宫必须包含一个起点(S)");
            }
            if (!hasExit) {
                throw new IllegalArgumentException("迷宫必须包含一个出口(E)");
            }

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("迷宫文件未找到: " + args[0]);
        }
    }

   /**
     * 处理用户输入控制玩家移动
     * @return true表示游戏继续，false表示退出游戏
     */
    public boolean processInput() {
        Scanner inputScanner = new Scanner(System.in);
        String input = inputScanner.nextLine().toLowerCase();

        switch (input) {
            case "w": // 向上移动
                movePlayer(0, -1);
                break;
            case "a": // 向左移动
                movePlayer(-1, 0);
                break;
            case "s": // 向下移动
                movePlayer(0, 1);
                break;
            case "d": // 向右移动
                movePlayer(1, 0);
                break;
            case "m": // 显示地图
                displayMap();
                break;
            case "q": // 退出游戏
                return false;
            default:
                System.out.println("无效输入。使用WASD移动，M查看地图，Q退出");
        }

        // 检查是否到达出口
        if (maze[playerY][playerX] == EXIT) {
            System.out.println("恭喜！你已成功逃出迷宫！");
            return false;
        }

        return true;
    }

    /**
     * 移动玩家位置
     * @param dx X方向移动量
     * @param dy Y方向移动量
     */
    private void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;

        if (isValidMove(newX, newY)) {
            playerX = newX;
            playerY = newY;
            System.out.println("移动成功");
        } else {
            System.out.println("无法移动至该位置");
        }
    }

    /**
     * 显示当前地图和玩家位置
     */
    private void displayMap() {
        System.out.println("当前地图（X为你的位置）:");

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == playerX && y == playerY) {
                    System.out.print(PLAYER); // 玩家位置显示为X
                } else {
                    System.out.print(maze[y][x]);
                }
            }
            System.out.println();
        }
    }

    /**
     * 验证移动是否有效
     * @param x 目标位置X坐标
     * @param y 目标位置Y坐标
     * @return true如果移动有效，false如果无效
     */
    private boolean isValidMove(int x, int y) {
        // 检查是否在迷宫范围内
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        // 检查目标位置是否是墙
        if (maze[y][x] == WALL) {
            return false;
        }

        return true;
    }

    /**
     * 开始游戏主循环
     */
    public void startGame() {
        gameOver = false;

        System.out.println("欢迎来到迷宫游戏！");
        System.out.println("使用WASD移动，M查看地图，Q退出");

        while (!gameOver) {
            System.out.print("请输入移动方向: ");
            gameOver = !processInput();
        }
    }

    /**
     * 程序主入口
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        String[] test = {"/reg_5x5.txt"};
        MazeGame game = new MazeGame();

        try {
            game.loadMaze(test);
            game.startGame();
        } catch (IllegalArgumentException e) {
            System.err.println("错误: " + e.getMessage());
            System.exit(1);
        }
    }

}
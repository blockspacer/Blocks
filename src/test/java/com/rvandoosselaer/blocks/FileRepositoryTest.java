package com.rvandoosselaer.blocks;

import com.jme3.asset.DesktopAssetManager;
import com.jme3.math.FastMath;
import com.simsilica.mathd.Vec3i;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author rvandoosselaer
 */

public class FileRepositoryTest {

    @BeforeAll
    public static void setUp() {
        BlocksConfig.initialize(new DesktopAssetManager(true));
    }

    @Test
    public void testSaveAndLoadFullChunk() {
        BlocksConfig config = BlocksConfig.getInstance();
        int blockArrayLenght = config.getChunkSize().x * config.getChunkSize().y * config.getChunkSize().z;

        Chunk chunk = Chunk.createAt(new Vec3i(0, 0, 0));
        Block[] blocks = new Block[blockArrayLenght];
        IntStream.range(0, blockArrayLenght).forEach(i -> blocks[i] = config.getBlockRegistry().get("grass"));
        chunk.setBlocks(blocks);
        chunk.update();

        FileRepository repository = new FileRepository(Paths.get(System.getProperty("user.home"), ".blocks", "repository-a"));
        boolean result = repository.save(chunk);
        assertTrue(result);

        Chunk loadedChunk = repository.load(new Vec3i(0, 0, 0));
        assertEquals(loadedChunk.getLocation(), chunk.getLocation());
        assertEquals(loadedChunk.getBlocks().length, chunk.getBlocks().length);

        cleanup(repository.getPath());
    }

    @Test
    public void testSaveAndLoadEmptyChunk() {
        BlocksConfig config = BlocksConfig.getInstance();
        int blockArrayLength = config.getChunkSize().x * config.getChunkSize().y * config.getChunkSize().z;

        Chunk chunk = Chunk.createAt(new Vec3i(0, 0, 0));
        chunk.setBlocks(new Block[blockArrayLength]);
        chunk.update();

        FileRepository repository = new FileRepository(Paths.get(System.getProperty("user.home"), ".blocks", "repository-b"));
        boolean result = repository.save(chunk);
        assertTrue(result);

        Chunk loadedChunk = repository.load(new Vec3i(0, 0, 0));
        assertEquals(loadedChunk.getLocation(), chunk.getLocation());
        assertEquals(loadedChunk.getBlocks().length, chunk.getBlocks().length);

        cleanup(repository.getPath());
    }

    @Test
    public void testSaveAndLoadChunk() {
        BlocksConfig config = BlocksConfig.getInstance();
        int blockArrayLength = config.getChunkSize().x * config.getChunkSize().y * config.getChunkSize().z;

        Chunk chunk = Chunk.createAt(new Vec3i(0, 0, 0));
        chunk.setBlocks(new Block[blockArrayLength]);
        int random = FastMath.nextRandomInt(0, blockArrayLength - 1);
        chunk.getBlocks()[random] = config.getBlockRegistry().get("grass");
        chunk.update();

        FileRepository repository = new FileRepository(Paths.get(System.getProperty("user.home"), ".blocks", "repository-c"));
        boolean result = repository.save(chunk);
        assertTrue(result);

        Chunk loadedChunk = repository.load(new Vec3i(0, 0, 0));
        assertEquals(loadedChunk.getLocation(), chunk.getLocation());
        assertEquals(loadedChunk.getBlocks().length, chunk.getBlocks().length);
        assertEquals(loadedChunk.getBlocks()[random], chunk.getBlocks()[random]);

        cleanup(repository.getPath());
    }

    private void cleanup(Path path) {
        try {
            // remove all files
            Files.list(path).forEach(this::silentDelete);
            // remove folder
            Files.delete(path);
        } catch (IOException e) {
            // ignore
        }
    }

    private void silentDelete(Path file) {
        try {
            Files.delete(file);
        } catch (IOException e) {
            // ignore
        }
    }
}

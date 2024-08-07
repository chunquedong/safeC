//
// Copyright (c) 2024, chunquedong
// Licensed under the Academic Free License version 3.0
//
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import sc2.compiler.CompilerLog;
import sc2.compiler.ast.Token;
import sc2.compiler.parser.Tokenizer;

/**
 *
 * @author yangjiandong
 */
public class TokenizerTest {

    public TokenizerTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void test() throws IOException {
        Path path = Path.of("res/code/testStruct.sc");
        String src = Files.readString(path);
        
        CompilerLog log = new CompilerLog();
        Tokenizer toker = new Tokenizer(log, path.toString(), src);
        ArrayList<Token> toks = toker.tokenize();
        System.out.println(toks);
        log.printError();
        assertTrue(log.errors.size() == 0);
    }
}

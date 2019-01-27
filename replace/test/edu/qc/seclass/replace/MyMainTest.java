package edu.qc.seclass.replace;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class MyMainTest
{

  private ByteArrayOutputStream outStream;
  private ByteArrayOutputStream errStream;
  private PrintStream outOrig;
  private PrintStream errOrig;
  private Charset charset = StandardCharsets.UTF_8;

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  @Before
  public void setUp() throws Exception
  {
    outStream = new ByteArrayOutputStream();
    PrintStream out = new PrintStream(outStream);
    errStream = new ByteArrayOutputStream();
    PrintStream err = new PrintStream(errStream);
    outOrig = System.out;
    errOrig = System.err;
    System.setOut(out);
    System.setErr(err);
  }

  @After
  public void tearDown() throws Exception
  {
    System.setOut(outOrig);
    System.setErr(errOrig);
  }

  // Some utilities

  private File createTmpFile() throws IOException
  {
    File tmpfile = temporaryFolder.newFile();
    tmpfile.deleteOnExit();
    return tmpfile;
  }

  private File createInputFile1() throws Exception
  {
    File file1 = createTmpFile();
    FileWriter fileWriter = new FileWriter(file1);

    fileWriter.write("Hey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!");

    fileWriter.close();
    return file1;
  }

  private File createInputFile2() throws Exception
  {
    File file1 = createTmpFile();
    FileWriter fileWriter = new FileWriter(file1);

    fileWriter.write("Hey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice");

    fileWriter.close();
    return file1;
  }

  private File createInputFile3() throws Exception
  {
    File file1 = createTmpFile();
    FileWriter fileWriter = new FileWriter(file1);

    fileWriter.write("Hey Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123");

    fileWriter.close();
    return file1;
  }

  private File createInputFile4() throws Exception
  {
    File file1 = createTmpFile();
    FileWriter fileWriter = new FileWriter(file1);

    fileWriter.write("abcdefghijklmnop");

    fileWriter.close();
    return file1;
  }

  private File createInputFile5() throws Exception
  {
    File file1 = createTmpFile();
    FileWriter fileWriter = new FileWriter(file1);

    fileWriter.write("only one a");

    fileWriter.close();
    return file1;
  }

  private String getFileContent( String filename )
  {
    String content = null;
    try
    {
      content = new String(Files.readAllBytes(Paths.get(filename)), charset);
    } catch (IOException e)
    {
      e.printStackTrace();
    }
    return content;
  }

  // Actual test cases

  // Implementation for test frame #1
  /*
  Number of files provided :  None
   */
  @Test
  public void mainTest1()
  {
    String args[] = { "-i", "Hey", "Hello", "--" };
    Main.main(args);
    assertEquals("Usage: Replace [-b] [-f] [-l] [-i] <from> <to> -- <filename> [<filename>]*", errStream.toString().trim());
  }

  // implementation for test frame #4
  /*
  Number of occurrences of the "from" string in the file :  None
   */
  @Test
  public void mainTest2() throws Exception
  {
    File inputFile1 = createInputFile1();

    String args[] = { "-i", "PRESENCE", "Hai", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation for test frame #5
  /*
  Length of the From string :  Zero
   */
  @Test
  public void mainTest3() throws Exception
  {
    File inputFile1 = createInputFile1();

    String args[] = { "-i", "", "Hai", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #6
  /*
  Length of the To string :  Zero
   */
  @Test
  public void mainTest4() throws Exception
  {
    File inputFile1 = createInputFile1();

    String args[] = { "-i", "Hey", "", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = " Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \" joe\" again!";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #8
  /*
  Number of arguments :  Zero
   */
  @Test
  public void mainTest5() throws Exception
  {
    File inputFile1 = createInputFile1();

    String args[] = { "--", "test", "PROJECTTEST", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a PROJECTTEST file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting PROJECTTEST cases for the project...\n" +
        "And let's say \"hey joe\" again!";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #9
  /*
  -b, creates a backup copy of each file :  Create backup
   */
  @Test
  public void mainTest6() throws Exception
  {
    File inputFile1 = createInputFile2();

    String args[] = { "-b", "-f", "Joe", "Garry", "--", inputFile1.getPath()  };
    Main.main(args);

    String expected1 = "Hey Garry,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertTrue(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #10
  @Test
  public void mainTest7() throws Exception
  {
    File inputFile1 = createInputFile4();

    String args[] = { "-f", "-l", "-i", "a", "b", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "bbcdefghijklmnop";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #11
  @Test
  public void mainTest8() throws Exception
  {
    File inputFile1 = createInputFile4();

    String args[] = { "-f", "-l", "d", "c", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "abccefghijklmnop";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #12
  @Test
  public void mainTest9() throws Exception
  {
    File inputFile1 = createInputFile4();

    String args[] = { "-f", "-i", "D", "c", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "abccefghijklmnop";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #13
  @Test
  public void mainTest10() throws Exception
  {
    File inputFile1 = createInputFile4();

    String args[] = { "-f", "j", "i", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "abcdefghiiklmnop";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #14
  @Test
  public void mainTest11() throws Exception
  {
    File inputFile1 = createInputFile4();

    String args[] = { "-l", "-i", "--", "I", "j", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "abcdefghjjklmnop";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #15
  @Test
  public void mainTest12() throws Exception
  {
    File inputFile1 = createInputFile4();

    String args[] = { "-l", "p", "o", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "abcdefghijklmnoo";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #16
  @Test
  public void mainTest13() throws Exception
  {
    File inputFile1 = createInputFile4();

    String args[] = { "--", "p", "o", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "abcdefghijklmnoo";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #17
  @Test
  public void mainTest14() throws Exception
  {
    File inputFile1 = createInputFile4();

    String args[] = { "-f", "-l", "-i", "p", "MORETHANONE", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "abcdefghijklmnoMORETHANONE";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #24
  @Test
  public void mainTest15() throws Exception
  {
    File inputFile1 = createInputFile4();

    String args[] = { "-f", "-l", "-i", "abcdefg", "a", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "ahijklmnop";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #31
  @Test
  public void mainTest16() throws Exception
  {
    File inputFile1 = createInputFile4();

    String args[] = { "-f", "-l", "-i", "abc", "xyz", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "xyzdefghijklmnop";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Implementation of test frame #38
  @Test
  public void mainTest17() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-l", "-i", "s", "h", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "Thih is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's hay \"hey joe\" again!";
    String expected2 = "Hey Joe,\n" +
        "Thih is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and sayh \"hey Joe\" twice";
    String expected3 = "Hey Joe, have you learned your abc and 123?\n" +
        "It ih important to know your abc and 123," +
        "so you should htudy it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #39
  @Test
  public void mainTest18() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-l", "t", "p", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a pest file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And lep's say \"hey joe\" again!";
    String expected2 = "Hey Joe,\n" +
        "This is anopher test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" pwice";
    String expected3 = "Hey Joe, have you learned your abc and 123?\n" +
        "Ip is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat wiph me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #40
  @Test
  public void mainTest19() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-i", "H", "P", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Pey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "Pey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";
    String expected3 = "Pey Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #41
  @Test
  public void mainTest20() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "e", "a", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hay Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "Hay Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";
    String expected3 = "Hay Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #42
  @Test
  public void mainTest21() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-l", "-i", "N", "l", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" agail!";
    String expected2 = "Hey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "ald says \"hey Joe\" twice";
    String expected3 = "Hey Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc ald 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #43
  @Test
  public void mainTest22() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-l", "i", "M", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" agaMn!";
    String expected2 = "Hey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twMce";
    String expected3 = "Hey Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat wMth me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #44
  @Test
  public void mainTest23() throws Exception
  {
    File inputFile1 = createInputFile4();
    File inputFile2 = createInputFile5();

    String args[] = { "--", "a", "z", "--", inputFile1.getPath(), inputFile2.getPath() };
    Main.main(args);

    String expected1 = "zbcdefghijklmnop";
    String expected2 = "only one z";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
  }

  // Implementation of test frame #45
  @Test
  public void mainTest24() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-l", "-i", "h", "Teddy", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Teddyey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"Teddyey joe\" again!";
    String expected2 = "Teddyey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"Teddyey Joe\" twice";
    String expected3 = "Teddyey Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat witTeddy me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation for test frame #46
  @Test
  public void mainTest25() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-l", "t", "TEE", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a TEEest file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And leTEE's say \"hey joe\" again!";
    String expected2 = "Hey Joe,\n" +
        "This is anoTEEher test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" TEEwice";
    String expected3 = "Hey Joe, have you learned your abc and 123?\n" +
        "ITEE is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat wiTEEh me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #47
  @Test
  public void mainTest26() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-i", "h", "Hello", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Helloey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "Helloey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";
    String expected3 = "Helloey Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #48
  @Test
  public void mainTest27() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "e", "EHH", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "HEHHy Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "HEHHy Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";
    String expected3 = "HEHHy Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #49
  @Test
  public void mainTest28() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-l", "-i", "j", "Andrew", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey Andrewoe\" again!";
    String expected2 = "Hey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Andrewoe\" twice";
    String expected3 = "Hey Andrewoe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #50
  @Test
  public void mainTest29() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-l", "J", "Hello", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Hellooe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "Hey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Hellooe\" twice";
    String expected3 = "Hey Hellooe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #51
  @Test
  public void mainTest30() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "--", "H", "Ultimate", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Ultimateey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "Ultimateey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";
    String expected3 = "Ultimateey Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #52
  @Test
  public void mainTest31() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-l", "-i", "Joe", "o", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey o,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey o\" again!";
    String expected2 = "Hey o,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey o\" twice";
    String expected3 = "Hey o, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #53
  @Test
  public void mainTest32() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-l", "Hey", "H", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "H Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "H Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";
    String expected3 = "H Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #54
  @Test
  public void mainTest33() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-i", "Hey", "o", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "o Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "o Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";
    String expected3 = "o Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #55
  @Test
  public void mainTest34() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "Hey", "p", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "p Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "p Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";
    String expected3 = "p Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation for test frame #56
  @Test
  public void mainTest35() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-l", "-i", "Hey", "m", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"m joe\" again!";
    String expected2 = "Hey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"m Joe\" twice";
    String expected3 = "m Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #57
  @Test
  public void mainTest36() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-l", "joe", "j", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey j\" again!";
    String expected2 = "Hey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";
    String expected3 = "Hey Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #59
  @Test
  public void mainTest37() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-l", "-i", "Hey", "Hello", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hello Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"Hello joe\" again!";
    String expected2 = "Hello Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"Hello Joe\" twice";
    String expected3 = "Hello Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation for test frame #60
  @Test
  public void mainTest38() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-l", "Joe", "Andrew", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Andrew,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "Hey Andrew,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Andrew\" twice";
    String expected3 = "Hey Andrew, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation for test frame #61
  @Test
  public void mainTest39() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "-i", "Hey", "Hello", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hello Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "Hello Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";
    String expected3 = "Hello Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation for test frame #62
  @Test
  public void mainTest40() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-f", "Hey", "Hello", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hello Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey joe\" again!";
    String expected2 = "Hello Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hey Joe\" twice";
    String expected3 = "Hello Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation for test frame #63
  @Test
  public void mainTest41() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-l", "-i", "Hey", "Hello", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"Hello joe\" again!";
    String expected2 = "Hey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"Hello Joe\" twice";
    String expected3 = "Hello Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Implementation of test frame #64
  @Test
  public void mainTest42() throws Exception
  {
    File inputFile1 = createInputFile1();
    File inputFile2 = createInputFile2();
    File inputFile3 = createInputFile3();

    String args[] = { "-l", "hey", "hello", "--", inputFile1.getPath(), inputFile2.getPath(), inputFile3.getPath() };
    Main.main(args);

    String expected1 = "Hey Joe,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hello joe\" again!";
    String expected2 = "Hey Joe,\n" +
        "This is another test file for the replace project\n" +
        "that contains an ordered list:\n" +
        "1) Item a\n" +
        "2) Item b\n" +
        "...\n" +
        "and says \"hello Joe\" twice";
    String expected3 = "Hey Joe, have you learned your abc and 123?\n" +
        "It is important to know your abc and 123," +
        "so you should study it\n" +
        "and then repeat with me: abc and 123";

    String actual1 = getFileContent(inputFile1.getPath());
    String actual2 = getFileContent(inputFile2.getPath());
    String actual3 = getFileContent(inputFile3.getPath());

    assertEquals("The files differ!", expected1, actual1);
    assertEquals("The files differ!", expected2, actual2);
    assertEquals("The files differ!", expected3, actual3);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile2.getPath() + ".bck")));
    assertFalse(Files.exists(Paths.get(inputFile3.getPath() + ".bck")));
  }

  // Newly created test case. Purpose: making sure -i parameter works properly with length >= 2 for from and to string.
  @Test
  public void mainTest43() throws Exception
  {
    File inputFile1 = createInputFile1();

    String args[] = { "-i", "Joe", "Hai", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "Hey Hai,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey Hai\" again!";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Newly created test case. Purpose: making sure -i parameter works properly with length 1 for from string and length >= 2 for to string.
  @Test
  public void mainTest44() throws Exception
  {
    File inputFile1 = createInputFile1();

    String args[] = { "-i", "o", "Hai", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "Hey JHaie,\n" +
        "This is a test file fHair the replace prHaiject\n" +
        "Let's make sure it has at least a few lines\n" +
        "sHai that we can create interesting test cases fHair the prHaiject...\n" +
        "And let's say \"hey jHaie\" again!";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }

  // Newly created test case. Purpose: making sure -i parameter works properly with length >= 2 for from string and length 1 for to string.
  @Test
  public void mainTest45() throws Exception
  {
    File inputFile1 = createInputFile1();

    String args[] = { "-i", "Joe", "p", "--", inputFile1.getPath() };
    Main.main(args);

    String expected1 = "Hey p,\n" +
        "This is a test file for the replace project\n" +
        "Let's make sure it has at least a few lines\n" +
        "so that we can create interesting test cases for the project...\n" +
        "And let's say \"hey p\" again!";

    String actual1 = getFileContent(inputFile1.getPath());

    assertEquals("The files differ!", expected1, actual1);

    assertFalse(Files.exists(Paths.get(inputFile1.getPath() + ".bck")));
  }
}
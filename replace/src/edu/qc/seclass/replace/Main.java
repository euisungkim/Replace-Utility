/*
Name: Daniel Eui Sung Kim
 */

package edu.qc.seclass.replace;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Paths;
import java.io.FileWriter;

public class Main
{
  private static Charset charset = StandardCharsets.UTF_8;

  public static void main( String[] args )
  {
    // TODO: Empty skeleton method
    ArrayList<String> arrayList = new ArrayList<>();
    Collections.addAll(arrayList, args);
    replace(arrayList);
  }

  private static void replace( ArrayList<String> args )
  {
    // replace OPT <from> <to> -- <filename> [<filename>]*
    if ( args.isEmpty() )
    {
      usage();
      return;
    }

    ArrayList<String> to = new ArrayList<>();
    ArrayList<String> from = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();
    boolean fileNamesExist = false;
    boolean backup = false;
    boolean replaceFirst = false;
    boolean replaceLast = false;
    boolean searchAndReplaceAll = false;

    // get index before file
    int indexBeforeFile = args.lastIndexOf("--");

    // if there's no specified file names or "--" not found then throw usage();
    if ( indexBeforeFile == args.size()-1 || indexBeforeFile < 0 )
    {
      usage();
      return;
    }

    // check if file name exists "--"
    for ( int i = indexBeforeFile; i < args.size(); i++ )
    {
      if ( args.get(i).equals("--") && i < args.size()-1 )
        fileNamesExist = true;
      else if ( fileNamesExist )
        fileNames.add(args.get(i));
    }

    // if file name does not exist then usage then return
    if ( !fileNamesExist )
    {
      usage();
      return;
    }

    // check for OPT
    int optIndexEnd = 0;
    for ( int i = 0; i < args.size(); i++ )
    {
      if ( args.get(i).equals("-f") )
        replaceFirst = true;
      else if ( args.get(i).equals("-l") )
        replaceLast = true;
      else if ( args.get(i).equals("-i") )
        searchAndReplaceAll = true;
      else if ( args.get(i).equals("-b") )
        backup = true;
      // index after OPT found
      else if ( args.get(i).equals("--")
          || !args.get(i).equals("-f")
          || !args.get(i).equals("-l")
          || !args.get(i).equals("-i")
          || !args.get(i).equals("-b") )
      {
        optIndexEnd = i;
        break;
      }
    }

    // if it starts with a "--" then we need to start one index after
    if ( args.get(optIndexEnd).equals("--") ) optIndexEnd++;

    // use j to distinguish from and to strings, from has to be the start
    for ( int i = optIndexEnd, j = 0; i < indexBeforeFile; i++ )
    {
      if ( j == 0 )
      {
        from.add(args.get(i));
        j++;
      }
      else if ( j == 1 )
      {
        to.add(args.get(i));
        j--;
      }
      else
      {
        break;
      }
    }

    // if number of strings in from array and to array is different, or
    // if there's no from string or
    // there's no to string then throw usage();
    if ( from.size() == 0 || from.size() != to.size() || to.size() == 0 )
    {
      usage();
      return;
    }

    // basic test done, get to replacing strings
    replace(from, to, fileNames, replaceFirst, replaceLast, searchAndReplaceAll, backup);
  }

  private static void replace( ArrayList<String> from, ArrayList<String> to, ArrayList<String> fileNames,
                               boolean replaceFirst, boolean replaceLast,
                               boolean searchAndReplaceAll, boolean backup )
  {
    // from and to array sizes are the same; i can use either. traverse through the array.
    for ( int j = 0; j < from.size(); j++ )
    {
      // if from string is not specified then throw usage();
      if ( from.get(j).equals("") )
      {
        usage();
        return;
      }
      // traverse through each file and replace from string to to string
      for ( int i = 0; i < fileNames.size(); i++ )
      {
        String inputFile = getFileContent(fileNames.get(i));
        String outputFile = "";

        try
        {
          // backup inputFile
          if ( backup )
          {
            // get path of the backup file
            Path path = Paths.get(fileNames.get(i) + ".bck");
            // if backup file does not exist then
            if ( Files.notExists(path) )
            {
              // create backup file
              File backupFile = new File(fileNames.get(i) + ".bck");
              FileWriter fileWriter = new FileWriter(backupFile, false);
              fileWriter.write(inputFile);
              fileWriter.close();
            } else // if it exists then do not perform replace
            {
              String paths = path.toString();
              int position = paths.lastIndexOf("/") + 1;
              int dotPosition = paths.lastIndexOf(".");
              System.err.println("Not performing replace for " + paths.substring(position, dotPosition) + ": Backup file already exists");
            }
          }
          // replace first and last, non case-sensitive
          if ( replaceFirst && replaceLast && searchAndReplaceAll )
          {
            String inputFileSb = new StringBuilder(inputFile).reverse().toString();
            String fromSb = new StringBuilder(from.get(j)).reverse().toString();
            String toSb = new StringBuilder(to.get(j)).reverse().toString();
            outputFile = new StringBuilder(inputFileSb.replaceFirst("(?i)" + fromSb, toSb)).reverse().toString();
            outputFile = outputFile.replaceFirst("(?i)" + from.get(j), to.get(j));
          }
          // replace first non case-sensitive
          else if ( replaceFirst && searchAndReplaceAll )
          {
            // using regex
            outputFile = inputFile.replaceFirst("(?i)" + from.get(j), to.get(j));
          }
          // replace last non case-sensitive
          else if ( replaceLast && searchAndReplaceAll )
          {
            // reverse the string to take advantage of .replaceFirst() built-in function
            String inputFileSb = new StringBuilder(inputFile).reverse().toString();
            String fromSb = new StringBuilder(from.get(j)).reverse().toString();
            String toSb = new StringBuilder(to.get(j)).reverse().toString();
            outputFile = new StringBuilder(inputFileSb.replaceFirst("(?i)" + fromSb, toSb)).reverse().toString();
          }
          // replace first and last, case-sensitive
          else if ( replaceFirst && replaceLast )
          {
            // replace the first one first, then
            // reverse the string to take advantage of .replaceFirst() built-in function
            outputFile = inputFile.replaceFirst(from.get(j), to.get(j));
            String inputFileSb = new StringBuilder(outputFile).reverse().toString();
            String fromSb = new StringBuilder(from.get(j)).reverse().toString();
            String toSb = new StringBuilder(to.get(j)).reverse().toString();
            outputFile = new StringBuilder(inputFileSb.replaceFirst(fromSb, toSb)).reverse().toString();
          }
          // replace first only, case-sensitive
          else if ( replaceFirst )
          {
            outputFile = inputFile.replaceFirst(from.get(j), to.get(j));
          }
          // replace last only, case-sensitive
          else if ( replaceLast )
          {
            String inputFileSb = new StringBuilder(inputFile).reverse().toString();
            String fromSb = new StringBuilder(from.get(j)).reverse().toString();
            String toSb = new StringBuilder(to.get(j)).reverse().toString();
            outputFile = new StringBuilder(inputFileSb.replaceFirst(fromSb, toSb)).reverse().toString();
          }
          // replace all, non case-sensitive
          else if ( searchAndReplaceAll )
          {
            outputFile = inputFile.replaceAll("(?i)" + from.get(j), to.get(j));
          }
          // no opt specified, replace all with case-sensitive
          else
          {
            outputFile = inputFile.replaceAll(from.get(j), to.get(j));
          }

          // finished replacing, write to a same file name with outputFile
          FileWriter fw = new FileWriter(fileNames.get(i));
          fw.write(outputFile);
          fw.close();
        } catch (Exception e)
        {
          // if file not found then print an error
          String path = Paths.get(fileNames.get(i)).toString();
          int position = path.lastIndexOf("/") + 1;
          System.err.println("File " + path.substring(position) + " not found");
        }
      }
    }
  }

  private static String getFileContent(String filename) {
    String content = null;
    try {
      content = new String(Files.readAllBytes(Paths.get(filename)), charset);
    } catch (IOException e) {
      // empty exception
    }
    return content;
  }

  private static void usage()
  {
    System.err.println("Usage: Replace [-b] [-f] [-l] [-i] <from> <to> -- " + "<filename> [<filename>]*");
  }
}

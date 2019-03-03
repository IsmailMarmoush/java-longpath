/*
 * Copyright 2011 Ismail Marmoush
 * 
 * This file is part of JLongPath.
 * 
 * JLongPath is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License Version 3 as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * 
 * JLongPath is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * JLongPath. If not, see http://www.gnu.org/licenses/.
 * 
 * For More Information Please Visit http://marmoush.com
 */
package com.marmoush.JLongPath;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * The Class Copier.
 */
public class Copier
{

    /** The keep copying. */
    private boolean keepCopying = true;

    /** The file being copied. */
    private String fileBeingCopied = "";

    /** The n files copied. */
    private long nFilesCopied = 0;

    /** The n files total. */
    private long nFilesTotal = 0;

    /**
     * Copy.
     * 
     * @param srcDirPath the src dir path
     * @param destDirPath the dest dir path
     * @return the long
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public long copy(String srcDirPath, String destDirPath) throws IOException
    {
	File src = new File(srcDirPath);
	File dest = new File(destDirPath + File.separator + src.getName());
	long result = doCopy(src, dest);
	return result;
    }

    /**
     * Copy file.
     * 
     * @param origSrc the orig src
     * @param from the from
     * @param origDestination the orig destination
     * @throws IOException
     */
    public void copyFile(File origSrc, File from, File origDestination)
	throws IOException
    {
	File destination = relativize(origSrc, from, origDestination);
	this.fileBeingCopied = from.getPath();
	Files.copy(from.toPath(), destination.toPath(),
	    StandardCopyOption.REPLACE_EXISTING,
	    StandardCopyOption.COPY_ATTRIBUTES);
    }

    /**
     * Count files.
     * 
     * @param filePath the file path
     * @return the long
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public long countFiles(String filePath) throws IOException
    {
	File srcFile = new File(filePath);
	this.nFilesTotal = 0;
	Files.walkFileTree(srcFile.toPath(), new FileVisitor<Path>()
	{
	    @Override
	    public FileVisitResult postVisitDirectory(Path dir, IOException e)
		throws IOException
	    {
		return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult preVisitDirectory(Path dir,
		BasicFileAttributes attrs) throws IOException
	    {
		if (isKeepCopying())
		{
		    return FileVisitResult.CONTINUE;
		} else
		{
		    return FileVisitResult.TERMINATE;
		}
	    }

	    @Override
	    public FileVisitResult visitFile(Path file,
		BasicFileAttributes attrs) throws IOException
	    {
		nFilesTotal++;
		return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFileFailed(Path file, IOException exc)
		throws IOException
	    {
		return FileVisitResult.CONTINUE;
	    }
	});
	return nFilesTotal;
    }

    /**
     * Gets the file being copied.
     * 
     * @return the file being copied
     */
    public String getFileBeingCopied()
    {
	return fileBeingCopied;
    }

    /**
     * Gets the n files copied.
     * 
     * @return the n files copied
     */
    public long getnFilesCopied()
    {
	return nFilesCopied;
    }

    /**
     * Gets the percentage.
     * 
     * @param nFilesTotal the n files total
     * @return the percentage
     */
    public int getPercentage(long nFilesTotal)
    {
	return (int) ((getnFilesCopied() * 100l) / nFilesTotal);
    }

    /**
     * Checks if is keep copying.
     * 
     * @return true, if is keep copying
     */
    public boolean isKeepCopying()
    {
	return keepCopying;
    }

    /**
     * Relativize.
     * 
     * @param mainSrc the main src
     * @param childSrc the child src
     * @param dest the dest
     * @return the file
     */
    public File relativize(File mainSrc, File childSrc, File dest)
    {
	Path f = mainSrc.toPath().relativize(childSrc.toPath());
	return new File(dest.getPath() + File.separator + f.toString());
    }

    /**
     * Sets the keep copying.
     * 
     * @param cancelled the new keep copying
     */
    public void setKeepCopying(boolean cancelled)
    {
	this.keepCopying = cancelled;
    }

    /**
     * Do copy.
     * 
     * @param srcFile the src file
     * @param destFile the dest file
     * @return the long
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private long doCopy(final File srcFile, final File destFile)
	throws IOException
    {
	this.nFilesCopied = 0;
	Files.walkFileTree(srcFile.toPath(), new FileVisitor<Path>()
	{
	    @Override
	    public FileVisitResult postVisitDirectory(Path dir, IOException e)
		throws IOException
	    {
		return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult preVisitDirectory(Path dir,
		BasicFileAttributes attrs) throws IOException
	    {
		if (isKeepCopying())
		{
		    copyFile(srcFile, dir.toFile(), destFile);
		    return FileVisitResult.CONTINUE;
		} else
		{
		    return FileVisitResult.TERMINATE;

		}
	    }

	    @Override
	    public FileVisitResult visitFile(Path file,
		BasicFileAttributes attrs) throws IOException
	    {
		copyFile(srcFile, file.toFile(), destFile);
		nFilesCopied++;
		return FileVisitResult.CONTINUE;
	    }

	    @Override
	    public FileVisitResult visitFileFailed(Path file, IOException exc)
		throws IOException
	    {
		return FileVisitResult.CONTINUE;
	    }
	});
	return nFilesCopied;
    }
}

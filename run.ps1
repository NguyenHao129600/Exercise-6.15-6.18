param(
    [ValidateSet('all', '15', '16', '17', '18', 'test')]
    [string]$Mode = 'all'
)
$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$OutputEncoding = [Console]::OutputEncoding
$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$outputDir = Join-Path $projectRoot 'out'
$sourceDir = Join-Path $projectRoot 'src\main\java'
if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw 'Chua tim thay javac. Hay cai dat JDK 21 va kiem tra PATH.'
}
if (-not (Test-Path $outputDir)) { New-Item -ItemType Directory -Path $outputDir | Out-Null }
$javaFiles = @(Get-ChildItem -Path $sourceDir -Recurse -Filter '*.java' -File | ForEach-Object { $_.FullName })
& javac -encoding UTF-8 -d $outputDir @javaFiles
if ($LASTEXITCODE -ne 0) { throw 'Bien dich that bai.' }
& java -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -cp $outputDir vn.designbook.chapter6.Main $Mode
if ($LASTEXITCODE -ne 0) { throw 'Chay chuong trinh that bai.' }

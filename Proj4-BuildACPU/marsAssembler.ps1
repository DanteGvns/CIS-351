param(
    [Parameter(Mandatory = $true, Position = 0)]
    [string]$AssemblyFile,

    [Alias('o')]
    [ValidateNotNullOrEmpty()]
    [string]$OutputFile
)

$ErrorActionPreference = 'Stop'
$dlunit = Join-Path $PSScriptRoot '..\JLS\DLUnit.jar'

if (-not (Test-Path -LiteralPath $dlunit -PathType Leaf)) {
    throw "DLUnit.jar must be in the repository's JLS directory: $dlunit"
}

if (-not (Test-Path -LiteralPath $AssemblyFile -PathType Leaf)) {
    throw "Assembly file not found: $AssemblyFile"
}

$java = Get-Command java -CommandType Application -ErrorAction Stop | Select-Object -First 1
if ($PSBoundParameters.ContainsKey('OutputFile')) {
    $machineCode = @(& $java.Source -cp $dlunit edu.gvsu.dlunit.MARSAssembler $AssemblyFile)
    $assemblerExitCode = $LASTEXITCODE
    if ($assemblerExitCode -ne 0) {
        throw "MARS assembler failed with exit code $assemblerExitCode."
    }

    $machineCode | Out-File -LiteralPath $OutputFile -Encoding ascii
    exit 0
}

& $java.Source -cp $dlunit edu.gvsu.dlunit.MARSAssembler $AssemblyFile
exit $LASTEXITCODE

param()

$ErrorActionPreference = 'Stop'
$dlunit = Join-Path $PSScriptRoot '..\JLS\DLUnit.jar'
$cpu = Join-Path $PSScriptRoot 'PHSingleCycleCPU.jls'
$tests = @(Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'tests') -Filter '*.a' |
    Sort-Object Name)
$java = Get-Command java -CommandType Application -ErrorAction Stop
if (-not (Test-Path -LiteralPath $dlunit -PathType Leaf)) {
    throw "DLUnit.jar not found: $dlunit"
}
if (-not (Test-Path -LiteralPath $cpu -PathType Leaf)) {
    throw "CPU circuit not found: $cpu"
}
if ($tests.Count -eq 0) {
    throw 'No assembly tests found.'
}

$failed = @()
Push-Location $PSScriptRoot
try {
    foreach ($test in $tests) {
        Write-Host "`n=== $($test.Name) ==="
        & $java.Source -jar $dlunit $cpu builtin.SingleCycleCPUTest --param $test.FullName
        if ($LASTEXITCODE -ne 0) {
            $failed += $test.Name
        }
    }
} finally {
    Pop-Location
}

Write-Host "`nPassed $($tests.Count - $failed.Count)/$($tests.Count) tests."
if ($failed.Count -gt 0) {
    Write-Host "Failed: $($failed -join ', ')"
    exit 1
}
exit 0

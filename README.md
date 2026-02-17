![Compatible with GraalVM](https://img.shields.io/badge/compatible_with-GraalVM-green)

# Escriba

![egypt scribe](https://upload.wikimedia.org/wikipedia/commons/thumb/a/a7/Sitting_Egyptian_Scribe_%28drawing%29.svg/500px-Sitting_Egyptian_Scribe_%28drawing%29.svg.png)

Scribes were highly valued members of Egyptian society. They studied for many years to learn to read and write.

## Development

Migration Creation:
`lein run -m pg.migration.cli -c migration.config.edn create --slug 'customers'`

Apply all Migrations:
`lein run -m pg.migration.cli -c migration.config.edn migrate --all`

## License

Copyright © 2025 Bruno do Nascimento Maciel.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.

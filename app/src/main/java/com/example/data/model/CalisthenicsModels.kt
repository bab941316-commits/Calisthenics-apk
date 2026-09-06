package com.example.data.model

enum class DifficultyLevel(val displayName: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced"),
    MASTERY("Mastery")
}

enum class MovementCategory(val displayName: String) {
    PUSH("Push"),
    PULL("Pull"),
    CORE("Core"),
    LEGS("Legs"),
    SKILLS("Balance & Skills")
}

data class MovementPhase(
    val timestampSeconds: Int,
    val phaseName: String,
    val formFocus: String
)

data class Exercise(
    val id: String,
    val name: String,
    val category: MovementCategory,
    val difficulty: DifficultyLevel,
    val defaultSets: Int,
    val defaultRepsOrSeconds: String,
    val isTimedHold: Boolean = false,
    val holdDurationSeconds: Int = 30,
    val targetMuscles: List<String>,
    val description: String,
    val formCues: List<String>,
    val commonMistakes: List<String>,
    val progressionRank: Int, // 1 (easiest) to 5 (elite)
    val progressionChain: String,
    val videoUrl: String = "",
    val videoEmbedUrl: String = "",
    val videoDurationSeconds: Int = 25,
    val movementPhases: List<MovementPhase> = emptyList()
)

data class RoutineExercise(
    val exerciseId: String,
    val sets: Int,
    val targetRepsOrSeconds: String,
    val restSeconds: Int = 45,
    val isTimed: Boolean = false,
    val durationSeconds: Int = 30
)

data class WorkoutRoutine(
    val id: String,
    val title: String,
    val subtitle: String,
    val difficulty: DifficultyLevel,
    val estimatedMinutes: Int,
    val category: MovementCategory,
    val exercises: List<RoutineExercise>,
    val description: String
)

object CalisthenicsData {
    val allExercises: List<Exercise> = listOf(
        // Push Progressions
        Exercise(
            id = "wall_pushups",
            name = "Wall Push-ups",
            category = MovementCategory.PUSH,
            difficulty = DifficultyLevel.BEGINNER,
            defaultSets = 3,
            defaultRepsOrSeconds = "15 reps",
            targetMuscles = listOf("Pectorals", "Anterior Deltoids", "Triceps"),
            description = "The foundational push movement for conditioning connective tissues and establishing scapular control.",
            formCues = listOf("Keep body in a straight plank line", "Elbows at 45-degree angle", "Squeeze chest at peak contraction"),
            commonMistakes = listOf("Flaring elbows out 90 degrees", "Sagging lower back"),
            progressionRank = 1,
            progressionChain = "Push-up Chain",
            videoUrl = "https://www.youtube.com/watch?v=a6YZb3863qc",
            videoEmbedUrl = "https://www.youtube.com/embed/a6YZb3863qc",
            videoDurationSeconds = 20,
            movementPhases = listOf(
                MovementPhase(0, "Setup", "Hands flat on wall at chest height, shoulder-width apart"),
                MovementPhase(5, "Descent", "Inhale, bend elbows at 45 degrees, keeping body rigid"),
                MovementPhase(12, "Peak Depth", "Nose/chest touches wall, maintain tight core"),
                MovementPhase(17, "Lockout", "Exhale, press firmly away until arms are fully extended")
            )
        ),
        Exercise(
            id = "knee_pushups",
            name = "Knee Push-ups",
            category = MovementCategory.PUSH,
            difficulty = DifficultyLevel.BEGINNER,
            defaultSets = 3,
            defaultRepsOrSeconds = "12 reps",
            targetMuscles = listOf("Chest", "Triceps", "Core"),
            description = "Reduces bodyweight load to approximately 50% while building muscle endurance and joint resilience.",
            formCues = listOf("Pivot from knees", "Brace core like a rigid plank", "Full range of motion: chest 1 inch from floor"),
            commonMistakes = listOf("Piking hips upward", "Incomplete depth"),
            progressionRank = 2,
            progressionChain = "Push-up Chain",
            videoUrl = "https://www.youtube.com/watch?v=jWxvty2KROs",
            videoEmbedUrl = "https://www.youtube.com/embed/jWxvty2KROs",
            videoDurationSeconds = 20,
            movementPhases = listOf(
                MovementPhase(0, "Knee Plank", "Knees grounded, straight line from knees through hips to head"),
                MovementPhase(6, "Descent", "Lower chest toward floor with elbows tracking back at 45°"),
                MovementPhase(13, "Bottom Pause", "Hover 1 inch above floor with active chest contraction"),
                MovementPhase(18, "Press Up", "Drive through palms, locking out without piking hips")
            )
        ),
        Exercise(
            id = "standard_pushups",
            name = "Standard Push-ups",
            category = MovementCategory.PUSH,
            difficulty = DifficultyLevel.BEGINNER,
            defaultSets = 3,
            defaultRepsOrSeconds = "10-15 reps",
            targetMuscles = listOf("Pectorals", "Triceps", "Core", "Anterior Delts"),
            description = "The gold standard bodyweight push exercise. Moves roughly 64% of total body mass.",
            formCues = listOf("Hands shoulder-width apart", "Lock out elbows at top", "Tuck pelvis slightly (posterior pelvic tilt)"),
            commonMistakes = listOf("Dipping head forward", "Flaring elbows", "Hyperextending lumbar spine"),
            progressionRank = 3,
            progressionChain = "Push-up Chain",
            videoUrl = "https://www.youtube.com/watch?v=IODxDxX7oi4",
            videoEmbedUrl = "https://www.youtube.com/embed/IODxDxX7oi4",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "High Plank Setup", "Shoulder-width hands, feet together, pelvis posteriorly tilted"),
                MovementPhase(7, "Controlled Descent", "Inhale, retract scapulae, lower over 2 seconds"),
                MovementPhase(15, "Bottom Depth", "Chest touches floor lightly, elbows at 45° arrow angle"),
                MovementPhase(21, "Explosive Push", "Press up to protract scapulae at top with hollow body")
            )
        ),
        Exercise(
            id = "diamond_pushups",
            name = "Diamond Push-ups",
            category = MovementCategory.PUSH,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultSets = 3,
            defaultRepsOrSeconds = "8-12 reps",
            targetMuscles = listOf("Triceps Brachii", "Inner Chest", "Core"),
            description = "Close hand placement brings intense mechanical tension directly to the triceps and inner chest fibers.",
            formCues = listOf("Index fingers and thumbs forming a diamond", "Lower chest toward the hands", "Keep elbows tucked to ribs"),
            commonMistakes = listOf("Wrist collapse", "Partial reps"),
            progressionRank = 4,
            progressionChain = "Push-up Chain",
            videoUrl = "https://www.youtube.com/watch?v=J0DnG1_S92I",
            videoEmbedUrl = "https://www.youtube.com/embed/J0DnG1_S92I",
            videoDurationSeconds = 20,
            movementPhases = listOf(
                MovementPhase(0, "Diamond Grip", "Index fingers and thumbs touching directly under center chest"),
                MovementPhase(6, "Elbow Tuck", "Lower chest directly to diamond window, elbows pinned to sides"),
                MovementPhase(13, "Triceps Contraction", "Bottom stretch with wrists stabilized and core tight"),
                MovementPhase(18, "Lockout", "Drive through triceps to complete full elbow extension")
            )
        ),
        Exercise(
            id = "parallel_dips",
            name = "Parallel Bar Dips",
            category = MovementCategory.PUSH,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultSets = 3,
            defaultRepsOrSeconds = "8-10 reps",
            targetMuscles = listOf("Lower Chest", "Triceps", "Front Delts"),
            description = "Often referred to as the upper-body squat. Lifts 100% of bodyweight through shoulder extension.",
            formCues = listOf("Slight forward torso lean for chest focus", "Lower until upper arms are parallel to bars", "Lock out at top with depressed scapula"),
            commonMistakes = listOf("Shrugging shoulders up toward ears", "Dropping excessively deep without shoulder mobility"),
            progressionRank = 4,
            progressionChain = "Dips Chain",
            videoUrl = "https://www.youtube.com/watch?v=2z8JmcrW-As",
            videoEmbedUrl = "https://www.youtube.com/embed/2z8JmcrW-As",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Support Hold", "Straight arms, depress shoulders firmly away from ears"),
                MovementPhase(8, "Controlled Descent", "Lean torso forward 15° for chest bias, lower to 90° elbows"),
                MovementPhase(16, "Parallel Depth", "Upper arms reach parallel with bars, forearms vertical"),
                MovementPhase(22, "Press to Lockout", "Drive up forcefully through triceps and pecs to top support")
            )
        ),
        Exercise(
            id = "pike_pushups",
            name = "Pike Push-ups",
            category = MovementCategory.PUSH,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultSets = 3,
            defaultRepsOrSeconds = "8-10 reps",
            targetMuscles = listOf("Overhead Deltoids", "Upper Chest", "Triceps"),
            description = "Vertical pushing progression laying the vertical strength foundation for the Handstand Push-up.",
            formCues = listOf("Hips high in an inverted V", "Head travels forward past hands like a tripod", "Press through palms to return"),
            commonMistakes = listOf("Elbows flaring sideways", "Not leaning forward into the tripod position"),
            progressionRank = 4,
            progressionChain = "Overhead Chain",
            videoUrl = "https://www.youtube.com/watch?v=qT_Tz_vU_3Y",
            videoEmbedUrl = "https://www.youtube.com/embed/qT_Tz_vU_3Y",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Inverted V Setup", "Hips high in the air, weight shifted into shoulders"),
                MovementPhase(7, "Tripod Descent", "Lean forward so head forms triangle apex ahead of hands"),
                MovementPhase(16, "Crown to Floor", "Light touch of head to floor with elbows stacked over wrists"),
                MovementPhase(22, "Press Back", "Push upward and back through shoulders to return to high pike")
            )
        ),
        Exercise(
            id = "archer_pushups",
            name = "Archer Push-ups",
            category = MovementCategory.PUSH,
            difficulty = DifficultyLevel.ADVANCED,
            defaultSets = 3,
            defaultRepsOrSeconds = "6 reps/side",
            targetMuscles = listOf("Unilateral Chest", "Triceps", "Rotator Cuff"),
            description = "Unilateral pushing movement where one arm pushes while the straight assist arm guides stability.",
            formCues = listOf("Wide hand placement", "Shift weight across to working arm", "Extended arm stays straight and locked"),
            commonMistakes = listOf("Rotating hips", "Short changing depth on the working arm"),
            progressionRank = 5,
            progressionChain = "Push-up Chain",
            videoUrl = "https://www.youtube.com/watch?v=U36F_Kz0Vl8",
            videoEmbedUrl = "https://www.youtube.com/embed/U36F_Kz0Vl8",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Wide Grip Setup", "Extra wide hand placement, fingers pointing slightly outward"),
                MovementPhase(8, "Lateral Shift", "Lower body exclusively over the primary working arm"),
                MovementPhase(15, "Archer Extension", "Assist arm remains completely straight across the floor"),
                MovementPhase(21, "Press & Return", "Single-arm drive back to center, alternating sides smoothly")
            )
        ),

        // Pull Progressions
        Exercise(
            id = "australian_rows",
            name = "Australian (Inverted) Rows",
            category = MovementCategory.PULL,
            difficulty = DifficultyLevel.BEGINNER,
            defaultSets = 3,
            defaultRepsOrSeconds = "10-12 reps",
            targetMuscles = listOf("Rhomboids", "Middle Trapezius", "Latissimus Dorsi", "Biceps"),
            description = "Horizontal bodyweight pulling that establishes back retraction strength for beginner and intermediate athletes.",
            formCues = listOf("Body rigid like an inverted plank", "Pull chest firmly to the bar", "Squeeze shoulder blades together for 1s"),
            commonMistakes = listOf("Leading with hips instead of chest", "Limp neck position"),
            progressionRank = 1,
            progressionChain = "Pull-up Chain",
            videoUrl = "https://www.youtube.com/watch?v=OYUxXMGVuuU",
            videoEmbedUrl = "https://www.youtube.com/embed/OYUxXMGVuuU",
            videoDurationSeconds = 20,
            movementPhases = listOf(
                MovementPhase(0, "Hanging Plank", "Heels planted, body straight, arms extended beneath bar"),
                MovementPhase(6, "Scapular Retraction", "Initiate pull by squeezing shoulder blades together"),
                MovementPhase(12, "Chest to Bar", "Pull sternum to touch bar, pause for 1 second contraction"),
                MovementPhase(17, "Controlled Lowering", "Resist gravity back down to full arm extension without sagging")
            )
        ),
        Exercise(
            id = "scapular_pullups",
            name = "Scapular Pull-ups",
            category = MovementCategory.PULL,
            difficulty = DifficultyLevel.BEGINNER,
            defaultSets = 3,
            defaultRepsOrSeconds = "10 reps",
            targetMuscles = listOf("Lower Traps", "Serratus Anterior", "Rotator Cuff"),
            description = "Isolates scapular depression from a dead hang with locked elbows, teaching the crucial initial pull mechanics.",
            formCues = listOf("Arms stay straight throughout", "Pull shoulder blades down and back", "Hold 1 second at top"),
            commonMistakes = listOf("Bending elbows", "Swinging legs"),
            progressionRank = 2,
            progressionChain = "Pull-up Chain",
            videoUrl = "https://www.youtube.com/watch?v=q6t8k9uS7Q0",
            videoEmbedUrl = "https://www.youtube.com/embed/q6t8k9uS7Q0",
            videoDurationSeconds = 20,
            movementPhases = listOf(
                MovementPhase(0, "Dead Hang", "Hang from bar with completely straight arms and relaxed shoulders"),
                MovementPhase(6, "Depress Scapulae", "Pull shoulders down away from ears without bending elbows"),
                MovementPhase(13, "Top Retraction Hold", "Hold high chest position for 1 second, squeeze lower traps"),
                MovementPhase(18, "Controlled Release", "Slowly ease back down into full passive dead hang")
            )
        ),
        Exercise(
            id = "standard_pullups",
            name = "Strict Pull-ups",
            category = MovementCategory.PULL,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultSets = 3,
            defaultRepsOrSeconds = "6-10 reps",
            targetMuscles = listOf("Latissimus Dorsi", "Biceps", "Forearms", "Posterior Delts"),
            description = "The quintessential calisthenics test. Full range overhead pull from dead hang to chin clearing bar.",
            formCues = listOf("Overhand grip slightly wider than shoulders", "Drive elbows down to hips", "No kipping or leg swinging"),
            commonMistakes = listOf("Kipping with knees", "Partial range without full lockout at bottom"),
            progressionRank = 3,
            progressionChain = "Pull-up Chain",
            videoUrl = "https://www.youtube.com/watch?v=eGo4IYlbE5g",
            videoEmbedUrl = "https://www.youtube.com/embed/eGo4IYlbE5g",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Dead Hang Start", "Pronated overhand grip, active shoulders, core braced"),
                MovementPhase(7, "Elbow Drive", "Drive elbows down toward hips while keeping legs unswung"),
                MovementPhase(15, "Chin Over Bar", "Pull chin fully above bar level, elbows tight behind ribs"),
                MovementPhase(22, "Descent Control", "Lower down through full 2-second eccentric to dead hang")
            )
        ),
        Exercise(
            id = "chin_ups",
            name = "Chin-ups",
            category = MovementCategory.PULL,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultSets = 3,
            defaultRepsOrSeconds = "6-10 reps",
            targetMuscles = listOf("Biceps Brachii", "Lats", "Brachialis"),
            description = "Underhand supinated grip engages maximum bicep recruitment along with upper back lat activation.",
            formCues = listOf("Palms facing you", "Pull chin well above the bar", "Controlled 2-second descent"),
            commonMistakes = listOf("Dropping uncontrolled into dead hang", "Flaring knees"),
            progressionRank = 3,
            progressionChain = "Pull-up Chain",
            videoUrl = "https://www.youtube.com/watch?v=brhRXlOhsAM",
            videoEmbedUrl = "https://www.youtube.com/embed/brhRXlOhsAM",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Supinated Grip", "Palms facing toward body, shoulder-width apart"),
                MovementPhase(7, "Biceps & Lat Pull", "Pull chest toward bar with powerful bicep engagement"),
                MovementPhase(14, "Peak Contraction", "Chin cleanly clears bar, maximum upper arm flexion"),
                MovementPhase(21, "Eccentric Lower", "Slowly lower back down until arms are fully straight")
            )
        ),
        Exercise(
            id = "muscle_up",
            name = "Bar Muscle-up",
            category = MovementCategory.PULL,
            difficulty = DifficultyLevel.MASTERY,
            defaultSets = 3,
            defaultRepsOrSeconds = "3-5 reps",
            targetMuscles = listOf("Full Back", "Chest", "Triceps", "Explosive Core"),
            description = "The premier calisthenics skill combining explosive high pull, lightning transition over the bar, and dip lockout.",
            formCues = listOf("Explosive pull toward sternum", "Fast torso transition over the bar", "Press out smoothly into straight arm support"),
            commonMistakes = listOf("One arm at a time (chicken winging)", "Pulling straight up instead of slight curved bar path"),
            progressionRank = 5,
            progressionChain = "Pull-up Chain",
            videoUrl = "https://www.youtube.com/watch?v=j9_jZz1t1wA",
            videoEmbedUrl = "https://www.youtube.com/embed/j9_jZz1t1wA",
            videoDurationSeconds = 30,
            movementPhases = listOf(
                MovementPhase(0, "False Grip / Hollow Swing", "Controlled swing arc with tight core and firm grip"),
                MovementPhase(8, "Explosive High Pull", "Drive chest violently upward toward upper sternum"),
                MovementPhase(16, "Torso Transition", "Throw chest over bar synchronously with both arms"),
                MovementPhase(24, "Straight Arm Lockout", "Press out top dip to finish in tall support hold")
            )
        ),

        // Core & Levers
        Exercise(
            id = "plank_hold",
            name = "Plank Hold",
            category = MovementCategory.CORE,
            difficulty = DifficultyLevel.BEGINNER,
            defaultSets = 3,
            defaultRepsOrSeconds = "45s hold",
            isTimedHold = true,
            holdDurationSeconds = 45,
            targetMuscles = listOf("Rectus Abdominis", "Transverse Abdominis", "Glutes"),
            description = "Isometric core fundamental maintaining neutral spine and high intra-abdominal tension.",
            formCues = listOf("Forearms parallel", "Posterior pelvic tilt (tuck tailbone)", "Squeeze glutes and quads continuously"),
            commonMistakes = listOf("Sagging hips", "Piking butt in the air"),
            progressionRank = 1,
            progressionChain = "Core Chain",
            videoUrl = "https://www.youtube.com/watch?v=ASdvN_XEl_c",
            videoEmbedUrl = "https://www.youtube.com/embed/ASdvN_XEl_c",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Forearm Alignment", "Elbows directly beneath shoulders, forearms parallel"),
                MovementPhase(7, "Pelvic Tuck", "Posterior pelvic tilt, eliminate lower back arching"),
                MovementPhase(15, "Full Body Tension", "Contract glutes, quadriceps, and abdominal wall tightly"),
                MovementPhase(22, "Steady Breathing", "Maintain rigid hollow line while taking calm diaphragmatic breaths")
            )
        ),
        Exercise(
            id = "hollow_body",
            name = "Hollow Body Hold",
            category = MovementCategory.CORE,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultSets = 3,
            defaultRepsOrSeconds = "30s hold",
            isTimedHold = true,
            holdDurationSeconds = 30,
            targetMuscles = listOf("Deep Core", "Hip Flexors", "Intercostals"),
            description = "Gymnastics core cornerstone creating the banana body curve essential for handstands and levers.",
            formCues = listOf("Press lower back flat into the ground", "Arms extended overhead by ears", "Toes pointed and legs glued together"),
            commonMistakes = listOf("Lower back arching off the floor", "Holding breath"),
            progressionRank = 2,
            progressionChain = "Core Chain",
            videoUrl = "https://www.youtube.com/watch?v=44ScXWFaVBs",
            videoEmbedUrl = "https://www.youtube.com/embed/44ScXWFaVBs",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Floor Setup", "Lie flat, imprint lumbar spine completely into floor"),
                MovementPhase(7, "Lift Shoulders & Feet", "Raise shoulder blades and straight legs 6-8 inches off ground"),
                MovementPhase(15, "Gymnastics Arc", "Arms extended by ears, toes pointed, banana shape hold"),
                MovementPhase(22, "Anti-Extension Hold", "Belly button pulled to spine, zero gap beneath lower back")
            )
        ),
        Exercise(
            id = "hanging_leg_raises",
            name = "Hanging Leg Raises",
            category = MovementCategory.CORE,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultSets = 3,
            defaultRepsOrSeconds = "10-12 reps",
            targetMuscles = listOf("Lower Abs", "Hip Flexors", "Grip & Forearms"),
            description = "Dynamic hanging core lift targeting the lower abdominal wall and hip compression strength.",
            formCues = listOf("Active dead hang shoulders", "Raise legs to 90 degrees or touch toes to bar", "Control the lowering phase without pendulum swing"),
            commonMistakes = listOf("Using momentum swinging", "Bending knees excessively"),
            progressionRank = 3,
            progressionChain = "Core Chain",
            videoUrl = "https://www.youtube.com/watch?v=hdng3Nm1x_E",
            videoEmbedUrl = "https://www.youtube.com/embed/hdng3Nm1x_E",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Hanging Lock", "Active dead hang, depress scapula, zero body swing"),
                MovementPhase(8, "Compression Lift", "Flex hips and abs, lifting locked straight legs forward"),
                MovementPhase(15, "Toes to Bar Peak", "Reach horizontal 90° or touch toes to bar with curled pelvis"),
                MovementPhase(22, "Controlled Negative", "Lower legs slowly without letting momentum take over")
            )
        ),
        Exercise(
            id = "l_sit_hold",
            name = "L-Sit Hold",
            category = MovementCategory.CORE,
            difficulty = DifficultyLevel.ADVANCED,
            defaultSets = 3,
            defaultRepsOrSeconds = "20s hold",
            isTimedHold = true,
            holdDurationSeconds = 20,
            targetMuscles = listOf("Abdominals", "Hip Flexors", "Triceps", "Scapular Depressors"),
            description = "Classic static strength hold lifting the entire lower body into a 90-degree right angle from parallel bars or floor.",
            formCues = listOf("Depress shoulders strongly away from ears", "Legs fully locked straight", "Toes pointed and knees unbent"),
            commonMistakes = listOf("Slumped shoulders", "Bent knees touching down early"),
            progressionRank = 4,
            progressionChain = "Core Chain",
            videoUrl = "https://www.youtube.com/watch?v=IUZJoSP66HI",
            videoEmbedUrl = "https://www.youtube.com/embed/IUZJoSP66HI",
            videoDurationSeconds = 20,
            movementPhases = listOf(
                MovementPhase(0, "Support Setup", "Hands on bars or floor, lock elbows, depress shoulders"),
                MovementPhase(6, "Hip Lift", "Push floor down violently to hover hips above ground"),
                MovementPhase(12, "90-Degree L-Line", "Extend legs straight out parallel to the floor, toes pointed"),
                MovementPhase(17, "Compression Endurance", "Keep quads flexed and chest upright throughout hold")
            )
        ),
        Exercise(
            id = "front_lever",
            name = "Front Lever Hold",
            category = MovementCategory.CORE,
            difficulty = DifficultyLevel.MASTERY,
            defaultSets = 3,
            defaultRepsOrSeconds = "10s hold",
            isTimedHold = true,
            holdDurationSeconds = 10,
            targetMuscles = listOf("Latissimus Dorsi", "Core", "Posterior Chain", "Grip"),
            description = "Elite isometric hold suspending the body horizontally straight and parallel to the ground while hanging from bar.",
            formCues = listOf("Straight arm pull down like a straight arm pulldown", "Retract and depress scapula", "Full glute and core lock"),
            commonMistakes = listOf("Bending arms", "Arching lumbar spine"),
            progressionRank = 5,
            progressionChain = "Core Chain",
            videoUrl = "https://www.youtube.com/watch?v=Zf_k4-kGv3s",
            videoEmbedUrl = "https://www.youtube.com/embed/Zf_k4-kGv3s",
            videoDurationSeconds = 20,
            movementPhases = listOf(
                MovementPhase(0, "Bar Engagement", "Overhand grip, engage lats and straight-arm pulldown force"),
                MovementPhase(5, "Horizontal Pull", "Lever entire body backward until parallel to the floor"),
                MovementPhase(12, "Straight Line Hold", "Head, shoulders, hips, and ankles aligned in horizontal plane"),
                MovementPhase(17, "Scapula Retraction", "Maintain retracted, depressed scapula with zero hip sag")
            )
        ),

        // Legs
        Exercise(
            id = "bodyweight_squats",
            name = "Bodyweight Air Squats",
            category = MovementCategory.LEGS,
            difficulty = DifficultyLevel.BEGINNER,
            defaultSets = 3,
            defaultRepsOrSeconds = "20 reps",
            targetMuscles = listOf("Quadriceps", "Gluteus Maximus", "Hamstrings", "Calves"),
            description = "Lower body baseline movement pattern ensuring hip hinge, knee tracking, and ankle dorsiflexion mobility.",
            formCues = listOf("Feet shoulder-width apart", "Knees track over toes", "Crease of hip below knee joint for full depth"),
            commonMistakes = listOf("Knees caving inward (valgus collapse)", "Heels lifting off ground"),
            progressionRank = 1,
            progressionChain = "Squat Chain",
            videoUrl = "https://www.youtube.com/watch?v=aclHkVaku9U",
            videoEmbedUrl = "https://www.youtube.com/embed/aclHkVaku9U",
            videoDurationSeconds = 20,
            movementPhases = listOf(
                MovementPhase(0, "Stance Setup", "Feet slightly wider than hips, toes angled outward 15-30°"),
                MovementPhase(5, "Hip Hinge & Descent", "Sit hips back and down, knees tracking over toes"),
                MovementPhase(11, "Below Parallel", "Hip crease drops just below knee joint, chest tall"),
                MovementPhase(16, "Drive Up", "Press through midfoot and heels to stand tall, squeeze glutes")
            )
        ),
        Exercise(
            id = "bulgarian_split_squats",
            name = "Bulgarian Split Squats",
            category = MovementCategory.LEGS,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultSets = 3,
            defaultRepsOrSeconds = "10 reps/leg",
            targetMuscles = listOf("Quadriceps", "Glutes", "Adductors", "Balance"),
            description = "Elevated rear foot isolates each leg individually, eliminating imbalances and increasing unilateral strength.",
            formCues = listOf("Rear foot elevated on bench or chair", "Descend until front thigh is parallel", "Keep chest upright and core braced"),
            commonMistakes = listOf("Front foot placed too close causing excessive heel lift", "Torso collapsing forward"),
            progressionRank = 3,
            progressionChain = "Squat Chain",
            videoUrl = "https://www.youtube.com/watch?v=2C-uNgKwPLE",
            videoEmbedUrl = "https://www.youtube.com/embed/2C-uNgKwPLE",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Foot Elevation", "Rear laces resting on bench, front foot stepped out 2-3 feet"),
                MovementPhase(7, "Unilateral Descent", "Lower back knee straight down toward the ground"),
                MovementPhase(15, "90-Degree Front Thigh", "Front thigh parallel to ground, torso upright"),
                MovementPhase(21, "Front Foot Drive", "Drive up through front heel to complete single-leg extension")
            )
        ),
        Exercise(
            id = "pistol_squat",
            name = "Pistol Squat (Single Leg)",
            category = MovementCategory.LEGS,
            difficulty = DifficultyLevel.ADVANCED,
            defaultSets = 3,
            defaultRepsOrSeconds = "5-8 reps/leg",
            targetMuscles = listOf("Quads", "Glutes", "Ankle Stabilizers", "Core"),
            description = "The pinnacle of bodyweight leg strength, mobility, and single-leg balance.",
            formCues = listOf("Non-working leg extended straight forward", "Descend under control into deep rock bottom", "Drive through midfoot and heel to rise"),
            commonMistakes = listOf("Heel popping up", "Knee caving inward"),
            progressionRank = 5,
            progressionChain = "Squat Chain",
            videoUrl = "https://www.youtube.com/watch?v=qDcniqddTeE",
            videoEmbedUrl = "https://www.youtube.com/embed/qDcniqddTeE",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Single-Leg Balance", "Stand on one foot, opposite leg held straight forward"),
                MovementPhase(7, "Deep Descent", "Sit back into single-leg squat, arms forward for counter-balance"),
                MovementPhase(15, "Rock Bottom Mobility", "Hamstring touches calf, non-working leg clears floor"),
                MovementPhase(22, "Explosive Rise", "Drive hard through working foot heel to lock out standing")
            )
        ),

        // Balance & Skills
        Exercise(
            id = "crow_pose",
            name = "Crow Pose (Bakasana)",
            category = MovementCategory.SKILLS,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultSets = 3,
            defaultRepsOrSeconds = "25s hold",
            isTimedHold = true,
            holdDurationSeconds = 25,
            targetMuscles = listOf("Wrist Stabilizers", "Anterior Delts", "Core", "Adductors"),
            description = "Arm balance skill teaching weight shift into the hands, protraction, and balance over the wrists.",
            formCues = listOf("Spread fingers wide like starfish", "Knees resting high on back of triceps", "Look forward, not straight down"),
            commonMistakes = listOf("Looking back at feet causing face-plant risk", "Low hip position"),
            progressionRank = 2,
            progressionChain = "Handstand Chain",
            videoUrl = "https://www.youtube.com/watch?v=yYnS4Q_t71g",
            videoEmbedUrl = "https://www.youtube.com/embed/yYnS4Q_t71g",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Squat & Hand Placement", "Hands shoulder-width, fingers spread, knees high on triceps"),
                MovementPhase(7, "Forward Lean", "Shift weight forward into hands, gaze looking slightly ahead"),
                MovementPhase(15, "Float Feet", "Lift one foot then both, heels tight to glutes"),
                MovementPhase(22, "Protracted Dome", "Push ground away, round upper back and hold balance")
            )
        ),
        Exercise(
            id = "wall_handstand",
            name = "Wall Handstand Hold",
            category = MovementCategory.SKILLS,
            difficulty = DifficultyLevel.INTERMEDIATE,
            defaultSets = 3,
            defaultRepsOrSeconds = "35s hold",
            isTimedHold = true,
            holdDurationSeconds = 35,
            targetMuscles = listOf("Shoulders", "Upper Traps", "Core", "Wrists"),
            description = "Builds overhead shoulder stamina and strict straight-line body positioning against the wall.",
            formCues = listOf("Hands 6 inches from wall", "Push the floor away actively (elevate shoulders)", "Tuck ribs and squeeze glutes"),
            commonMistakes = listOf("Banana back arching", "Dropping head backward"),
            progressionRank = 3,
            progressionChain = "Handstand Chain",
            videoUrl = "https://www.youtube.com/watch?v=Z3kLpWpZ7qU",
            videoEmbedUrl = "https://www.youtube.com/embed/Z3kLpWpZ7qU",
            videoDurationSeconds = 25,
            movementPhases = listOf(
                MovementPhase(0, "Kickup / Wall Walk", "Hands 6 inches from wall, kick up with control"),
                MovementPhase(7, "Shoulder Elevation", "Shrug shoulders upward toward ears, push floor away"),
                MovementPhase(15, "Hollow Body Alignment", "Tuck pelvis, glutes tight, heels lightly resting on wall"),
                MovementPhase(22, "Finger Balance Drill", "Micro-press with fingertips to practice coming off wall")
            )
        ),
        Exercise(
            id = "freestanding_handstand",
            name = "Freestanding Handstand",
            category = MovementCategory.SKILLS,
            difficulty = DifficultyLevel.MASTERY,
            defaultSets = 3,
            defaultRepsOrSeconds = "30s hold",
            isTimedHold = true,
            holdDurationSeconds = 30,
            targetMuscles = listOf("Full Shoulder Girdle", "Finger Flexors", "Deep Core"),
            description = "The holy grail of bodyweight balance. Controlling center of mass purely with finger pressure and micro-adjustments.",
            formCues = listOf("Grip the ground with fingertips (cambered hands)", "Stack wrists, shoulders, hips, and ankles in one vertical line", "Breathe steadily"),
            commonMistakes = listOf("Not kicking up with enough momentum or overkicking", "Breaking line at hip"),
            progressionRank = 5,
            progressionChain = "Handstand Chain",
            videoUrl = "https://www.youtube.com/watch?v=XpkA_4_d-vA",
            videoEmbedUrl = "https://www.youtube.com/embed/XpkA_4_d-vA",
            videoDurationSeconds = 30,
            movementPhases = listOf(
                MovementPhase(0, "Cambered Hands & Kickup", "Fingers claw the ground, controlled kickup into line"),
                MovementPhase(8, "Joint Stack", "Wrists, elbows, shoulders, hips, and ankles in a vertical line"),
                MovementPhase(18, "Fingertip Corrections", "Press fingers to correct overbalance, palms to correct underbalance"),
                MovementPhase(26, "Controlled Step Down", "Lower one foot down softly under complete control")
            )
        )
    )

    val allRoutines: List<WorkoutRoutine> = listOf(
        WorkoutRoutine(
            id = "foundation_fullbody",
            title = "Bodyweight Foundation",
            subtitle = "Beginner Full Body Circuit",
            difficulty = DifficultyLevel.BEGINNER,
            estimatedMinutes = 15,
            category = MovementCategory.PUSH,
            description = "Master the primary movement fundamentals: push, pull, squat, and core stabilization.",
            exercises = listOf(
                RoutineExercise("standard_pushups", sets = 3, targetRepsOrSeconds = "10 reps", restSeconds = 45),
                RoutineExercise("australian_rows", sets = 3, targetRepsOrSeconds = "10 reps", restSeconds = 45),
                RoutineExercise("bodyweight_squats", sets = 3, targetRepsOrSeconds = "15 reps", restSeconds = 45),
                RoutineExercise("plank_hold", sets = 3, targetRepsOrSeconds = "35s hold", restSeconds = 45, isTimed = true, durationSeconds = 35)
            )
        ),
        WorkoutRoutine(
            id = "push_power_core",
            title = "Push & Upper Body Blast",
            subtitle = "Chest, Triceps & Front Delts",
            difficulty = DifficultyLevel.INTERMEDIATE,
            estimatedMinutes = 18,
            category = MovementCategory.PUSH,
            description = "High-density push routine combining horizontal, vertical, and dip mechanics with core control.",
            exercises = listOf(
                RoutineExercise("parallel_dips", sets = 3, targetRepsOrSeconds = "8 reps", restSeconds = 60),
                RoutineExercise("diamond_pushups", sets = 3, targetRepsOrSeconds = "10 reps", restSeconds = 45),
                RoutineExercise("pike_pushups", sets = 3, targetRepsOrSeconds = "8 reps", restSeconds = 60),
                RoutineExercise("hollow_body", sets = 3, targetRepsOrSeconds = "30s hold", restSeconds = 45, isTimed = true, durationSeconds = 30)
            )
        ),
        WorkoutRoutine(
            id = "pull_grip_mastery",
            title = "Pull & Grip Mastery",
            subtitle = "Lats, Biceps & Hanging Strength",
            difficulty = DifficultyLevel.INTERMEDIATE,
            estimatedMinutes = 20,
            category = MovementCategory.PULL,
            description = "Develop vertical pulling power and iron grip from dead hang to explosive chin above bar.",
            exercises = listOf(
                RoutineExercise("standard_pullups", sets = 3, targetRepsOrSeconds = "6-8 reps", restSeconds = 75),
                RoutineExercise("chin_ups", sets = 3, targetRepsOrSeconds = "6-8 reps", restSeconds = 60),
                RoutineExercise("australian_rows", sets = 3, targetRepsOrSeconds = "12 reps", restSeconds = 45),
                RoutineExercise("hanging_leg_raises", sets = 3, targetRepsOrSeconds = "10 reps", restSeconds = 60)
            )
        ),
        WorkoutRoutine(
            id = "core_static_holds",
            title = "Iron Core & Static Levers",
            subtitle = "L-Sit, Hollow Body & Compression",
            difficulty = DifficultyLevel.ADVANCED,
            estimatedMinutes = 14,
            category = MovementCategory.CORE,
            description = "Target gymnastic core compression, scapular depression, and anti-extension stamina.",
            exercises = listOf(
                RoutineExercise("l_sit_hold", sets = 3, targetRepsOrSeconds = "15s hold", restSeconds = 60, isTimed = true, durationSeconds = 15),
                RoutineExercise("hanging_leg_raises", sets = 3, targetRepsOrSeconds = "12 reps", restSeconds = 45),
                RoutineExercise("hollow_body", sets = 3, targetRepsOrSeconds = "35s hold", restSeconds = 45, isTimed = true, durationSeconds = 35),
                RoutineExercise("plank_hold", sets = 3, targetRepsOrSeconds = "60s hold", restSeconds = 45, isTimed = true, durationSeconds = 60)
            )
        ),
        WorkoutRoutine(
            id = "quick_7min_burner",
            title = "7-Minute Calisthenics Burner",
            subtitle = "High-Intensity Bodyweight Circuit",
            difficulty = DifficultyLevel.BEGINNER,
            estimatedMinutes = 7,
            category = MovementCategory.PUSH,
            description = "Short, intense full-body circuit designed for maximal calorie burn and metabolic conditioning.",
            exercises = listOf(
                RoutineExercise("standard_pushups", sets = 2, targetRepsOrSeconds = "15 reps", restSeconds = 25),
                RoutineExercise("bodyweight_squats", sets = 2, targetRepsOrSeconds = "20 reps", restSeconds = 25),
                RoutineExercise("knee_pushups", sets = 2, targetRepsOrSeconds = "12 reps", restSeconds = 25),
                RoutineExercise("plank_hold", sets = 2, targetRepsOrSeconds = "40s hold", restSeconds = 25, isTimed = true, durationSeconds = 40)
            )
        ),
        WorkoutRoutine(
            id = "skills_balance",
            title = "Handstand & Balance Skills",
            subtitle = "Handstand, Crow & Shoulder Stack",
            difficulty = DifficultyLevel.ADVANCED,
            estimatedMinutes = 16,
            category = MovementCategory.SKILLS,
            description = "Progressive balance practice to build wrist health, overhead stacking, and freestanding confidence.",
            exercises = listOf(
                RoutineExercise("crow_pose", sets = 3, targetRepsOrSeconds = "20s hold", restSeconds = 60, isTimed = true, durationSeconds = 20),
                RoutineExercise("wall_handstand", sets = 3, targetRepsOrSeconds = "30s hold", restSeconds = 60, isTimed = true, durationSeconds = 30),
                RoutineExercise("pike_pushups", sets = 3, targetRepsOrSeconds = "8 reps", restSeconds = 60),
                RoutineExercise("hollow_body", sets = 3, targetRepsOrSeconds = "30s hold", restSeconds = 45, isTimed = true, durationSeconds = 30)
            )
        )
    )

    fun getExerciseById(id: String): Exercise? = allExercises.find { it.id == id }
    fun getRoutineById(id: String): WorkoutRoutine? = allRoutines.find { it.id == id }
}

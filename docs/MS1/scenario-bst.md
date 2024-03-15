## SBST 1: Adding 1st MIT (Happy Path)
- Start the app by tapping the app icon. You should see the present date at top center, for example “Tuesday 1/23” with a ‘+’ button for adding MIT events.
- Click the ‘+’ button and you should see a keyboard pop up with a mic option for entering your MIT.
- Click the mic and state your MIT : “My First MIT”. You should see the text translation.
- Click the check-mark button and without latency, your MIT should be below the present date.
- Exit app.
- *contains US-1

## SBST 2: Adding Large Amount of MITs
- Start the app by tapping the app icon. You should see the present date at top center, for example “Tuesday 1/23” with a ‘+’ button for adding MIT events.
- Click the ‘+’ button and you should see a keyboard pop up with a mic option for entering your MIT.
- Click the mic and state your MIT : “1st MIT”. You should see the text translation.
- Click the check-mark button and without latency, your MIT should be below the present date.
- Repeat steps 2 and 3, MIT : “2nd MIT”.
- Repeat step 4, now the 2nd MIT should be directly below 1st MIT.
- Repeat the process until you have 10 MITs, 1st - 10th. They should be visible in the numbered hierarchy mirroring their input.
- Exit app.
- (Based on UI choices during iteration 1, they will all be visible, a portion will be visible (but allowed), or we shall cap the amount of MITs per day to maintain integrity of Success-List philosophy)
- *contains US-1/US-2/US-3

## SBST 3: Canceling adding an MIT
- Start the app by tapping the app icon. You should see the present date at top center, for example “Tuesday 1/23” with a ‘+’ button for adding MIT events.
- Click the ‘+’ button and you should see a keyboard pop up with a mic option for entering your MIT.
- Click the mic and state your MIT : “Test MIT”. The text should be visible and waiting for the pressing of the check-mark button. Do not press the check-mark button.
- Click the Cancel button. You should be immediately exited from the keyboard screen and returned to the default screen.
- No new MIT should have been added.
- Exit app.
 
- *contains US-1/US-2

## SBST 4: Display Order of MITs (completed / not completed)
- Start the app by tapping the app icon. You should see the present date at top center, for example “Tuesday 1/23” with a ‘+’ button for adding MIT events.
- Click the ‘+’ button and you should see a keyboard pop up with a mic option for entering your MIT.
- Click the mic and state your MIT : “1st MIT”. You should see the text translation.
- Click the check-mark button and without latency, your MIT should be below the present date.
- Repeat steps 2 and 3, MIT : “2nd MIT”.
- Repeat step 4, now the 2nd MIT should be directly below 1st MIT.
- Repeat steps 2 and 3, MIT : “3rd MIT”.
- Repeat step 4, now the 3rd MIT should be directly below the 2nd MIT.
- Now tap on the “1st MIT” once to mark it ‘completed’. It should appear beneath “3rd MIT” and have a strikethrough as such : 1t MIT.
- Now tap on the “2nd MIT” once to mark it ‘completed’. It should appear beneath “1st MIT” and have a strikethrough as such : 2nd MIT.
- Exit app.
 
- *contains US-1/US-2/US-3

## SBST 5: Change MIT status back to ‘incomplete’
- Start the app by tapping the app icon. You should see the present date at top center, for example “Tuesday 1/23” with a ‘+’ button for adding MIT events.
- Click the ‘+’ button and you should see a keyboard pop up with a mic option for entering your MIT.
- Click the mic and state your MIT : “1st MIT”. You should see the text translation.
- Click the check-mark button and without latency, your MIT should be below the present date.
- Repeat steps 2 and 3, MIT : “2nd MIT”.
- Repeat step 4, now the 2nd MIT should be directly below 1st MIT.
- Repeat steps 2 and 3, MIT : “3rd MIT”.
- Repeat step 4, now the 3rd MIT should be directly below the 2nd MIT.
- Now tap on the “1st MIT” once to mark it ‘completed’. It should appear beneath “3rd MIT” and have a strikethrough as such : 1t MIT.
- Now tap on 1st MIT. Without latency, you should see “1st MIT” above “2nd MIT”, which is now at the top of the uncompleted items. All else remains as such.
- Exit app.
 
- *contains US-1/US-2/US-3

## SBST 6: Change multiple MIT status back to ‘incomplete’
- Start the app by tapping the app icon. You should see the present date at top center, for example “Tuesday 1/23” with a ‘+’ button for adding MIT events.
- Click the ‘+’ button and you should see a keyboard pop up with a mic option for entering your MIT.
- Click the mic and state your MIT : “1st MIT”. You should see the text translation.
- Click the check-mark button and without latency, your MIT should be below the present date.
- Repeat steps 2 and 3, MIT : “2nd MIT”.
- Repeat step 4, now the 2nd MIT should be directly below 1st MIT.
- Repeat steps 2 and 3, MIT : “3rd MIT”.
- Repeat step 4, now the 3rd MIT should be directly below the 2nd MIT.
- Now tap on the “1st MIT” once to mark it ‘completed’. It should appear beneath “3rd MIT” and have a strikethrough as such : 1t MIT.
- Now tap on the “2nd MIT” once to mark it ‘completed’. It should appear above “1st MIT” and have a strikethrough as such : 2nd MIT.
- Now tap on 2nd MIT. Without latency, you should see “2nd MIT” above “3rd MIT”, which is now at the top of the uncompleted items. All else remains as such.
- Exit app.
 
- *contains US-1/US-2/US-3

## SBST 7: MIT Rollover and Update
- Start the app by tapping the app icon. You should see the present date at top center, for example “Tuesday 1/23” with a ‘+’ button for adding MIT events.
- Click the ‘+’ button and you should see a keyboard pop up with a mic option for entering your MIT.
- Click the mic and state your MIT : “ This MIT will be deleted”.
- Click the check-mark button and without latency, your MIT should be below the present date.
- Repeat this process with new MIT : “This MIT will remain”
- Now tap on the “This MIT will be deleted” MIT once to mark it ‘completed’. It should appear beneath “This MIT will remain” and have a strikethrough as such : This MIT will be deleted.
- Simulate the changing of date from “Tuesday 1/23” to “Tuesday 1/24”. You should see the date change accordingly and see that “This MIT will remain” MIT has remained and the ‘completed’ MIT This MIT will be deleted  has disappeared.
- Exit app.
 
- *contains US-1/US-2/US-3
